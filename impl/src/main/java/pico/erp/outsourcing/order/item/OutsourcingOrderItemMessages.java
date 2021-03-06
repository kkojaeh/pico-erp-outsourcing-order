package pico.erp.outsourcing.order.item;

import java.math.BigDecimal;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.process.ProcessId;
import pico.erp.project.ProjectId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.UnitKind;
import pico.erp.shared.event.Event;

public interface OutsourcingOrderItemMessages {

  interface Create {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class Request {

      @Valid
      @NotNull
      OutsourcingOrderItemId id;

      @NotNull
      OutsourcingOrder order;

      @Valid
      @NotNull
      ItemId itemId;

      @Valid
      @NotNull
      ProcessId processId;

      @Valid
      @NotNull
      ItemSpecCode itemSpecCode;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

      @NotNull
      UnitKind unit;

      @NotNull
      @Min(0)
      BigDecimal unitCost;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @NotNull
      ProjectId projectId;

      OutsourcingRequestId requestId;

      @NotNull
      OutsourcingOrderItemUnitCostEstimator unitCostEstimator;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Update {

    @Data
    class Request {

      @Valid
      @NotNull
      ProcessId processId;

      @Valid
      @NotNull
      ItemSpecCode itemSpecCode;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

      @NotNull
      @Min(0)
      BigDecimal unitCost;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @NotNull
      OutsourcingOrderItemUnitCostEstimator unitCostEstimator;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Delete {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Receive {

    @Data
    class Request {

      BigDecimal quantity;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Determine {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Send {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Reject {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Cancel {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

}
