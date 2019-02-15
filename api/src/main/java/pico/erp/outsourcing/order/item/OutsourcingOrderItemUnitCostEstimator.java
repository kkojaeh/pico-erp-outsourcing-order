package pico.erp.outsourcing.order.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.process.ProcessId;
import pico.erp.shared.data.UnitKind;

public interface OutsourcingOrderItemUnitCostEstimator {

  BigDecimal estimate(OutsourcingOrderItemContext context);

  interface OutsourcingOrderItemContext {

    ItemId getItemId();

    ProcessId getProcessId();

    BigDecimal getQuantity();

    UnitKind getUnit();

  }

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class OutsourcingOrderItemContextImpl implements OutsourcingOrderItemContext {

    ItemId itemId;

    ProcessId processId;

    BigDecimal quantity;

    UnitKind unit;

  }
}
