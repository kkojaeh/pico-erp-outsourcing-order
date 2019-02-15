package pico.erp.outsourcing.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
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

