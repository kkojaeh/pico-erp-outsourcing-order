package pico.erp.outsourcing.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.DeliveryRequests;
import pico.erp.delivery.DeliveryService;
import pico.erp.document.DocumentId;
import pico.erp.document.DocumentRequests;
import pico.erp.document.DocumentService;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemEvents;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemStatusKind;
import pico.erp.outsourcing.request.OutsourcingRequestRequests;
import pico.erp.outsourcing.request.OutsourcingRequestService;

@SuppressWarnings("unused")
@Component
public class OutsourcingOrderEventListener {

  private static final String LISTENER_NAME = "listener.outsourcing-order-event-listener";


  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private DocumentService documentService;

  @Lazy
  @Autowired
  private DeliveryService deliveryService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.DeterminedEvent.CHANNEL)
  public void onOrderDetermined(OutsourcingOrderEvents.DeterminedEvent event) {
    val id = event.getId();
    val order = outsourcingOrderService.get(id);
    if (order.getDraftId() != null) {
      documentService.delete(
        new DocumentRequests.DeleteRequest(order.getDraftId())
      );
    }
    val supplier = companyService.get(order.getSupplierId());
    val name = String.format("OO-%s-%s-%s",
      order.getCode().getValue(),
      supplier.getName(),
      DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
    );
    val draftId = DocumentId.generate();
    val draft = documentService.create(
      DocumentRequests.CreateRequest.builder()
        .id(draftId)
        .subjectId(OutsourcingOrderDraftDocumentSubjectDefinition.ID)
        .name(name)
        .key(id)
        .creatorId(order.getChargerId())
        .build()
    );
    val deliveryId = DeliveryId.generate();
    deliveryService.create(
      DeliveryRequests.CreateRequest.builder()
        .id(deliveryId)
        .subjectId(OutsourcingOrderDraftDeliverySubjectDefinition.ID)
        .key(id)
        .build()
    );
    outsourcingOrderService.prepareSend(
      OutsourcingOrderRequests.PrepareSendRequest.builder()
        .id(id)
        .draftId(draftId)
        .deliveryId(deliveryId)
        .build()
    );

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.CanceledEvent.CHANNEL)
  public void onOrderItemCanceled(OutsourcingOrderItemEvents.CanceledEvent event) {
    val orderItem = outsourcingOrderItemService.get(event.getId());
    val order = outsourcingOrderService.get(orderItem.getOrderId());
    if (order.isCancelable()) {
      val allCanceled = outsourcingOrderItemService.getAll(orderItem.getOrderId()).stream()
        .allMatch(item -> item.getStatus() == OutsourcingOrderItemStatusKind.CANCELED);
      if (allCanceled) {
        outsourcingOrderService.cancel(
          OutsourcingOrderRequests.CancelRequest.builder()
            .id(orderItem.getOrderId())
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.ReceivedEvent.CHANNEL)
  public void onOrderItemReceived(OutsourcingOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val orderItem = outsourcingOrderItemService.get(event.getId());
      val orderId = orderItem.getOrderId();

      val requestId = orderItem.getRequestId();
      if (requestId != null) {
        outsourcingRequestService.complete(
          OutsourcingRequestRequests.CompleteRequest.builder()
            .id(requestId)
            .build()
        );
      }

      val received = outsourcingOrderItemService.getAll(orderId).stream()
        .allMatch(item -> item.getStatus() == OutsourcingOrderItemStatusKind.RECEIVED);

      if (received) {
        outsourcingOrderService.receive(
          OutsourcingOrderRequests.ReceiveRequest.builder()
            .id(orderId)
            .build()
        );
      }

    }
  }

}

