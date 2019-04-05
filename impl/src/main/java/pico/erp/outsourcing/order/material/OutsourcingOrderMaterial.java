package pico.erp.outsourcing.order.material;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.shared.data.UnitKind;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutsourcingOrderMaterial implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  OutsourcingOrderMaterialId id;

  OutsourcingOrder order;

  ItemId itemId;

  ItemSpecCode itemSpecCode;

  BigDecimal quantity;

  UnitKind unit;

  String remark;

  CompanyId supplierId;

  OffsetDateTime estimatedSupplyDate;

  public OutsourcingOrderMaterial() {

  }

  public OutsourcingOrderMaterialMessages.Create.Response apply(
    OutsourcingOrderMaterialMessages.Create.Request request) {
    if (!request.getOrder().isUpdatable()) {
      throw new OutsourcingOrderMaterialExceptions.CannotCreateException();
    }
    this.id = request.getId();
    this.order = request.getOrder();
    this.itemId = request.getItemId();
    this.itemSpecCode = request.getItemSpecCode();
    this.quantity = request.getQuantity();
    this.unit = request.getUnit();
    this.remark = request.getRemark();
    this.supplierId = request.getSupplierId();
    this.estimatedSupplyDate = request.getEstimatedSupplyDate();

    return new OutsourcingOrderMaterialMessages.Create.Response(
      Arrays.asList(new OutsourcingOrderMaterialEvents.CreatedEvent(this.id))
    );
  }

  public OutsourcingOrderMaterialMessages.Update.Response apply(
    OutsourcingOrderMaterialMessages.Update.Request request) {
    if (!this.order.isUpdatable()) {
      throw new OutsourcingOrderMaterialExceptions.CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.unit = request.getUnit();
    this.remark = request.getRemark();
    this.supplierId = request.getSupplierId();
    this.estimatedSupplyDate = request.getEstimatedSupplyDate();
    return new OutsourcingOrderMaterialMessages.Update.Response(
      Arrays.asList(new OutsourcingOrderMaterialEvents.UpdatedEvent(this.id))
    );
  }

  public OutsourcingOrderMaterialMessages.Delete.Response apply(
    OutsourcingOrderMaterialMessages.Delete.Request request) {
    if (!this.order.isUpdatable()) {
      throw new OutsourcingOrderMaterialExceptions.CannotDeleteException();
    }
    return new OutsourcingOrderMaterialMessages.Delete.Response(
      Arrays.asList(new OutsourcingOrderMaterialEvents.DeletedEvent(this.id))
    );
  }

}
