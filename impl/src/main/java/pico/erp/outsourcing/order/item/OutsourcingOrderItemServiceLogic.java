package pico.erp.outsourcing.order.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemRequests.GenerateRequest;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
public class OutsourcingOrderItemServiceLogic implements OutsourcingOrderItemService {

  @Autowired
  private OutsourcingOrderItemRepository outsourcingOrderItemRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private OutsourcingOrderItemMapper mapper;

  @ComponentAutowired
  private ItemSpecService itemSpecService;

  @ComponentAutowired
  private ItemService itemService;

  @ComponentAutowired
  private OutsourcingRequestService outsourcingRequestService;

  @Override
  public void cancel(OutsourcingOrderItemRequests.CancelRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public OutsourcingOrderItemData create(OutsourcingOrderItemRequests.CreateRequest request) {
    val item = new OutsourcingOrderItem();
    val response = item.apply(mapper.map(request));
    if (outsourcingOrderItemRepository.exists(item.getId())) {
      throw new OutsourcingOrderItemExceptions.AlreadyExistsException();
    }
    val created = outsourcingOrderItemRepository.create(item);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(OutsourcingOrderItemRequests.DeleteRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.deleteBy(item.getId());
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void determine(OutsourcingOrderItemRequests.DetermineRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(OutsourcingOrderItemId id) {
    return outsourcingOrderItemRepository.exists(id);
  }

  @Override
  public boolean exists(OutsourcingRequestId requestId) {
    return outsourcingOrderItemRepository.exists(requestId);
  }

  @Override
  public void generate(GenerateRequest request) {
    request.getRequestIds().stream()
      .map(outsourcingRequestService::get)
      .map(outsourcingRequest -> {
        val id = OutsourcingOrderItemId.generate();
        val itemId = outsourcingRequest.getItemId();
        val item = itemService.get(itemId);
        val processId = outsourcingRequest.getProcessId();
        val itemSpecCode = outsourcingRequest.getItemSpecCode();
        val quantity = outsourcingRequest.getQuantity();
        val spareQuantity = outsourcingRequest.getSpareQuantity();
        val unit = item.getUnit();
        val unitCost = BigDecimal.ZERO;
        val remark = outsourcingRequest.getRemark();
        val projectId = outsourcingRequest.getProjectId();

        return OutsourcingOrderItemRequests.CreateRequest.builder()
          .id(id)
          .orderId(request.getId())
          .itemId(itemId)
          .processId(processId)
          .itemSpecCode(itemSpecCode)
          .quantity(quantity)
          .spareQuantity(spareQuantity)
          .unit(unit)
          .unitCost(unitCost)
          .remark(remark)
          .projectId(projectId)
          .requestId(outsourcingRequest.getId())
          .build();

      })
      .forEach(this::create);
    eventPublisher.publishEvent(
      new OutsourcingOrderItemEvents.GeneratedEvent(request.getId())
    );
  }

  @Override
  public OutsourcingOrderItemData get(OutsourcingOrderItemId id) {
    return outsourcingOrderItemRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
  }

  @Override
  public OutsourcingOrderItemData get(OutsourcingRequestId requestId) {
    return outsourcingOrderItemRepository.findBy(requestId)
      .map(mapper::map)
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
  }

  @Override
  public List<OutsourcingOrderItemData> getAll(OutsourcingOrderId orderId) {
    return outsourcingOrderItemRepository.findAllBy(orderId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void receive(OutsourcingOrderItemRequests.ReceiveRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(OutsourcingOrderItemRequests.RejectRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(OutsourcingOrderItemRequests.SendRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(OutsourcingOrderItemRequests.UpdateRequest request) {
    val item = outsourcingOrderItemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    outsourcingOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

}
