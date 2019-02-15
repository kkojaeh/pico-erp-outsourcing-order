package pico.erp.outsourcing.order.item;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.outsourcing.order.OutsourcingOrderEvents;
import pico.erp.outsourcing.order.OutsourcingOrderService;
import pico.erp.outsourcing.request.OutsourcingRequestEvents;
import pico.erp.outsourcing.request.OutsourcingRequestRequests;
import pico.erp.outsourcing.request.OutsourcingRequestService;

@SuppressWarnings("unused")
@Component
public class OutsourcingOrderItemEventListener {

  private static final String LISTENER_NAME = "listener.outsourcing-order-item-event-listener";

  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.CanceledEvent.CHANNEL)
  public void onOrderCanceled(OutsourcingOrderEvents.CanceledEvent event) {
    val orderId = event.getId();

    outsourcingOrderItemService.getAll(orderId).forEach(item -> {
      outsourcingOrderItemService.cancel(
        OutsourcingOrderItemRequests.CancelRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.DeterminedEvent.CHANNEL)
  public void onOrderDetermined(OutsourcingOrderEvents.DeterminedEvent event) {
    val orderId = event.getId();

    outsourcingOrderItemService.getAll(orderId).forEach(item -> {
      outsourcingOrderItemService.determine(
        OutsourcingOrderItemRequests.DetermineRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.GeneratedEvent.CHANNEL)
  public void onOrderGenerated(OutsourcingOrderEvents.GeneratedEvent event) {
    outsourcingOrderItemService.generate(
      OutsourcingOrderItemRequests.GenerateRequest.builder()
        .id(event.getId())
        .requestIds(event.getRequestIds())
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.CanceledEvent.CHANNEL)
  public void onOrderItemCanceled(OutsourcingOrderItemEvents.CanceledEvent event) {
    val item = outsourcingOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      val request = outsourcingRequestService.get(requestId);
      if (request.isProgressCancelable()) {
        outsourcingRequestService.cancelProgress(
          OutsourcingRequestRequests.CancelProgressRequest.builder()
            .id(requestId)
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.CreatedEvent.CHANNEL)
  public void onOrderItemCreated(OutsourcingOrderItemEvents.CreatedEvent event) {
    val item = outsourcingOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      outsourcingRequestService.plan(
        OutsourcingRequestRequests.PlanRequest.builder()
          .id(requestId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemReceived(OutsourcingOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val item = outsourcingOrderItemService.get(event.getId());
      val requestId = item.getRequestId();
      if (requestId != null) {
        outsourcingRequestService.complete(
          OutsourcingRequestRequests.CompleteRequest.builder()
            .id(requestId)
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemRejected(OutsourcingOrderItemEvents.RejectedEvent event) {
    val item = outsourcingOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      outsourcingRequestService.cancelProgress(
        OutsourcingRequestRequests.CancelProgressRequest.builder()
          .id(requestId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.SentEvent.CHANNEL)
  public void onOrderItemSent(OutsourcingOrderItemEvents.SentEvent event) {
    val item = outsourcingOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      outsourcingRequestService.progress(
        OutsourcingRequestRequests.ProgressRequest.builder()
          .id(requestId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.RejectedEvent.CHANNEL)
  public void onOrderRejected(OutsourcingOrderEvents.RejectedEvent event) {
    val orderId = event.getId();

    outsourcingOrderItemService.getAll(orderId).forEach(item -> {
      outsourcingOrderItemService.reject(
        OutsourcingOrderItemRequests.RejectRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderEvents.SentEvent.CHANNEL)
  public void onOrderSent(OutsourcingOrderEvents.SentEvent event) {
    val orderId = event.getId();

    outsourcingOrderItemService.getAll(orderId).forEach(item -> {
      outsourcingOrderItemService.send(
        OutsourcingOrderItemRequests.SendRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingRequestEvents.CanceledEvent.CHANNEL)
  public void onRequestCanceled(OutsourcingRequestEvents.CanceledEvent event) {
    val orderItem = outsourcingOrderItemService.get(event.getId());
    if (orderItem.isCancelable()) {
      outsourcingOrderItemService.cancel(
        OutsourcingOrderItemRequests.CancelRequest.builder()
          .id(orderItem.getId())
          .build()
      );
    }
  }

}
