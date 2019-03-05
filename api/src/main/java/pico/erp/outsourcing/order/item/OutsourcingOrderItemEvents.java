package pico.erp.outsourcing.order.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.shared.event.Event;

public interface OutsourcingOrderItemEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.created";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.updated";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeletedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.deleted";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class ReceivedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.received";

    private OutsourcingOrderItemId id;

    private BigDecimal quantity;

    private boolean completed;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeterminedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.determined";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CanceledEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.canceled";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class SentEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.sent";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RejectedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.rejected";

    private OutsourcingOrderItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class GeneratedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-order-item.generated";

    private OutsourcingOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }
}
