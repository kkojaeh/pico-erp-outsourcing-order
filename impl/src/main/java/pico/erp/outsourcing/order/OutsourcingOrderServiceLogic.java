package pico.erp.outsourcing.order;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.company.CompanyService;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.DeliveryRequests;
import pico.erp.delivery.DeliveryService;
import pico.erp.document.DocumentId;
import pico.erp.document.DocumentRequests;
import pico.erp.document.DocumentService;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.CancelRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.DetermineRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.GenerateRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.ReceiveRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.RejectRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.SendRequest;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.outsourcing.request.OutsourcingRequestStatusKind;
import pico.erp.shared.Public;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.shared.event.EventPublisher;
import pico.erp.warehouse.location.site.SiteService;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class OutsourcingOrderServiceLogic implements OutsourcingOrderService {

  @Autowired
  private OutsourcingOrderRepository outsourcingOrderRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private OutsourcingOrderMapper mapper;

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private SiteService siteService;

  @Lazy
  @Autowired
  private DocumentService documentService;

  @Lazy
  @Autowired
  private DeliveryService deliveryService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Override
  public void cancel(CancelRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public OutsourcingOrderData create(OutsourcingOrderRequests.CreateRequest request) {
    val outsourcingOrder = new OutsourcingOrder();
    val response = outsourcingOrder.apply(mapper.map(request));
    if (outsourcingOrderRepository.exists(outsourcingOrder.getId())) {
      throw new OutsourcingOrderExceptions.AlreadyExistsException();
    }
    val created = outsourcingOrderRepository.create(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void determine(DetermineRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val message = mapper.map(request);
    val previousDraftId = outsourcingOrder.getDraftId();
    val draftId = DocumentId.generate();
    val deliveryId = DeliveryId.generate();
    message.setDeliveryId(deliveryId);
    message.setDraftId(draftId);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
    if (previousDraftId != null) {
      documentService.delete(
        new DocumentRequests.DeleteRequest(previousDraftId)
      );
    }
    val supplier = companyService.get(outsourcingOrder.getSupplierId());
    val name = String.format("OO-%s-%s-%s",
      outsourcingOrder.getCode().getValue(),
      supplier.getName(),
      DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
    );
    documentService.create(
      DocumentRequests.CreateRequest.builder()
        .id(draftId)
        .subjectId(OutsourcingOrderDraftDocumentSubjectDefinition.ID)
        .name(name)
        .key(outsourcingOrder.getId())
        .creatorId(outsourcingOrder.getChargerId())
        .build()
    );
    deliveryService.create(
      DeliveryRequests.CreateRequest.builder()
        .id(deliveryId)
        .subjectId(OutsourcingOrderDraftDeliverySubjectDefinition.ID)
        .key(outsourcingOrder.getId())
        .build()
    );
  }

  @Override
  public boolean exists(OutsourcingOrderId id) {
    return outsourcingOrderRepository.exists(id);
  }

  @Override
  public OutsourcingOrderData generate(GenerateRequest request) {
    val purchaseRequests = request.getRequestIds().stream()
      .map(outsourcingRequestService::get)
      .collect(Collectors.toList());
    val supplierEquals = purchaseRequests.stream()
      .map(purchaseRequest -> "" + purchaseRequest.getSupplierId())
      .distinct()
      .limit(2)
      .count() < 2;
    val locationEquals = purchaseRequests.stream()
      .map(purchaseRequest -> "" + purchaseRequest.getReceiverId() + purchaseRequest
        .getReceiveSiteId())
      .distinct()
      .limit(2)
      .count() < 2;
    val allAccepted = purchaseRequests.stream()
      .allMatch(
        purchaseRequest -> purchaseRequest.getStatus() == OutsourcingRequestStatusKind.ACCEPTED);

    if (!supplierEquals || !locationEquals || !allAccepted) {
      throw new OutsourcingOrderExceptions.CannotGenerateException();
    }

    val dueDate = purchaseRequests.stream()
      .map(purchaseRequest -> purchaseRequest.getDueDate())
      .min(Comparator.comparing(d -> d))
      .orElseGet(() -> OffsetDateTime.now().plusDays(1));
    val supplierId = purchaseRequests.stream().findAny().get().getSupplierId();
    val collectedRemark = purchaseRequests.stream()
      .map(purchaseRequest -> Optional.ofNullable(purchaseRequest.getRemark()).orElse(""))
      .collect(Collectors.joining("\n"));
    val remark = collectedRemark
      .substring(0, Math.min(collectedRemark.length(), TypeDefinitions.REMARK_LENGTH));
    val purchaseRequest = purchaseRequests.get(0);
    val address = new Address();
    if (purchaseRequest.getReceiveSiteId() != null) {
      val siteAddress = siteService.get(purchaseRequest.getReceiveSiteId()).getAddress();
      address.setPostalCode(siteAddress.getPostalCode());
      address.setStreet(siteAddress.getStreet());
      address.setDetail(siteAddress.getDetail());
    }

    val created = create(
      OutsourcingOrderRequests.CreateRequest.builder()
        .id(request.getId())
        .dueDate(dueDate)
        .chargerId(request.getChargerId())
        .supplierId(supplierId)
        .receiveAddress(address)
        .receiverId(purchaseRequest.getReceiverId())
        .remark(remark)
        .build()
    );
    eventPublisher.publishEvent(
      new OutsourcingOrderEvents.GeneratedEvent(request.getRequestIds(), created.getId())
    );

    return created;
  }

  @Override
  public OutsourcingOrderData get(OutsourcingOrderId id) {
    return outsourcingOrderRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
  }

  @Override
  public void receive(ReceiveRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(RejectRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(SendRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(OutsourcingOrderRequests.UpdateRequest request) {
    val outsourcingOrder = outsourcingOrderRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new);
    val response = outsourcingOrder.apply(mapper.map(request));
    outsourcingOrderRepository.update(outsourcingOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

}
