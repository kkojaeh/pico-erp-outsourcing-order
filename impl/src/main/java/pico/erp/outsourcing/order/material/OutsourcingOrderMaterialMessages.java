package pico.erp.outsourcing.order.material;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.UnitKind;
import pico.erp.shared.event.Event;

public interface OutsourcingOrderMaterialMessages {

  interface Create {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class Request {

      @Valid
      @NotNull
      OutsourcingOrderMaterialId id;

      @NotNull
      OutsourcingOrder order;

      @NotNull
      ItemId itemId;

      @Valid
      @NotNull
      ItemSpecCode itemSpecCode;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      UnitKind unit;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @Valid
      CompanyId supplierId;

      @Future
      LocalDateTime estimatedSupplyDate;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Update {

    @Data
    class Request {

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      UnitKind unit;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @Valid
      CompanyId supplierId;

      @Future
      LocalDateTime estimatedSupplyDate;

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

}
