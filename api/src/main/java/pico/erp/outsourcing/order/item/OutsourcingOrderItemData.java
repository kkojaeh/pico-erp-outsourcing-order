package pico.erp.outsourcing.order.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.process.ProcessId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.UnitKind;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutsourcingOrderItemData {

  OutsourcingOrderItemId id;

  OutsourcingOrderId orderId;

  ItemId itemId;

  ItemSpecCode itemSpecCode;

  ProcessId processId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal receivedQuantity;

  BigDecimal estimatedUnitCost;

  BigDecimal unitCost;

  UnitKind unit;

  String remark;

  ProjectId projectId;

  OutsourcingRequestId requestId;

  OutsourcingOrderItemStatusKind status;

  boolean cancelable;

  boolean determinable;

  boolean receivable;

  boolean rejectable;

  boolean sendable;

  boolean updatable;


}
