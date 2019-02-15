package pico.erp.outsourcing.order.material;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.bom.BomService;
import pico.erp.bom.material.BomMaterialService;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemEvents;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialData;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialService;
import pico.erp.process.ProcessService;

@SuppressWarnings("unused")
@Component
public class OutsourcingOrderMaterialEventListener {

  private static final String LISTENER_NAME = "listener.outsourcing-request-material-event-listener";

  @Lazy
  @Autowired
  private ProcessService processService;

  @Lazy
  @Autowired
  private BomService bomService;

  @Lazy
  @Autowired
  private BomMaterialService bomMaterialService;

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Lazy
  @Autowired
  private OutsourcingOrderMaterialService outsourcingOrderMaterialService;

  @Lazy
  @Autowired
  private OutsourcingRequestMaterialService outsourcingRequestMaterialService;

  @Lazy
  @Autowired
  private ItemSpecService itemSpecService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingOrderItemEvents.GeneratedEvent.CHANNEL)
  public void onOrderItemGenerated(OutsourcingOrderItemEvents.GeneratedEvent event) {
    val orderId = event.getId();
    val orderItems = outsourcingOrderItemService.getAll(orderId);
    val orderItemKeys = orderItems.stream()
      .map(orderItem -> new OutsourcingItemKey(orderItem.getItemId(), orderItem.getItemSpecCode()))
      .collect(Collectors.toList());

    val materials = orderItems.stream().flatMap(orderItem -> {
      Stream<OutsourcingRequestMaterialData> datas = Stream.empty();
      val requestId = orderItem.getRequestId();
      if (requestId != null) {
        datas = outsourcingRequestMaterialService.getAll(requestId).stream()
          .filter(material -> !orderItemKeys
            .contains(new OutsourcingItemKey(material.getItemId(), material.getItemSpecCode())));
      }
      return datas;
    });

    materials.forEach(material -> {
      outsourcingOrderMaterialService.create(
        OutsourcingOrderMaterialRequests.CreateRequest.builder()
          .id(OutsourcingOrderMaterialId.generate())
          .orderId(orderId)
          .itemId(material.getItemId())
          .itemSpecCode(material.getItemSpecCode())
          .quantity(material.getQuantity())
          .unit(material.getUnit())
          .remark(material.getRemark())
          .supplierId(material.getSupplierId())
          .estimatedSupplyDate(material.getEstimatedSupplyDate())
          .build()
      );
    });

  }

  @Value
  @EqualsAndHashCode
  private static class OutsourcingItemKey {

    ItemId itemId;

    ItemSpecCode itemSpecCode;

  }

}
