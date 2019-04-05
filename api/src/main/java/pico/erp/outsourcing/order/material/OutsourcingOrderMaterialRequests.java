package pico.erp.outsourcing.order.material;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.UnitKind;

public interface OutsourcingOrderMaterialRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    OutsourcingOrderMaterialId id;

    @Valid
    @NotNull
    OutsourcingOrderId orderId;

    @Valid
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
    OffsetDateTime estimatedSupplyDate;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    OutsourcingOrderMaterialId id;

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
    OffsetDateTime estimatedSupplyDate;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DeleteRequest {

    @Valid
    @NotNull
    OutsourcingOrderMaterialId id;

  }

}
