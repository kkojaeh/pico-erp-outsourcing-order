package pico.erp.outsourcing.order.material;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.shared.data.UnitKind;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutsourcingOrderMaterialData {

  OutsourcingOrderMaterialId id;

  OutsourcingOrderId orderId;

  ItemId itemId;

  ItemSpecCode itemSpecCode;

  BigDecimal quantity;

  UnitKind unit;

  String remark;

  CompanyId supplierId;

  LocalDateTime estimatedSupplyDate;


}
