package pico.erp.outsourcing.order.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.outsourcing.order.OutsourcingOrderExceptions;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator.OutsourcingOrderItemContext;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator.OutsourcingOrderItemContextImpl;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.process.ProcessId;
import pico.erp.project.ProjectId;
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
public class OutsourcingOrderItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  OutsourcingOrderItemId id;

  OutsourcingOrder order;

  ItemId itemId;

  ProcessId processId;

  ItemSpecCode itemSpecCode;

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

  public OutsourcingOrderItem() {
  }

  public OutsourcingOrderItemMessages.Create.Response apply(
    OutsourcingOrderItemMessages.Create.Request request) {
    if (!request.getOrder().isUpdatable()) {
      throw new OutsourcingOrderItemExceptions.CannotCreateException();
    }
    this.id = request.getId();
    this.order = request.getOrder();
    this.itemId = request.getItemId();
    this.processId = request.getProcessId();
    this.itemSpecCode = request.getItemSpecCode();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.unitCost = request.getUnitCost();
    this.remark = request.getRemark();
    this.projectId = request.getProjectId();
    this.requestId = request.getRequestId();
    this.receivedQuantity = BigDecimal.ZERO;
    this.status = OutsourcingOrderItemStatusKind.DRAFT;
    this.estimatedUnitCost = request.getUnitCostEstimator().estimate(createContext());
    this.unit = request.getUnit();
    return new OutsourcingOrderItemMessages.Create.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.CreatedEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Update.Response apply(
    OutsourcingOrderItemMessages.Update.Request request) {
    if (!this.isUpdatable()) {
      throw new OutsourcingOrderItemExceptions.CannotUpdateException();
    }
    this.processId = request.getProcessId();
    this.itemSpecCode = request.getItemSpecCode();
    this.unitCost = request.getUnitCost();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.remark = request.getRemark();
    this.estimatedUnitCost = request.getUnitCostEstimator().estimate(createContext());
    return new OutsourcingOrderItemMessages.Update.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.UpdatedEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Delete.Response apply(
    OutsourcingOrderItemMessages.Delete.Request request) {
    if (!this.isUpdatable()) {
      throw new OutsourcingOrderItemExceptions.CannotDeleteException();
    }
    return new OutsourcingOrderItemMessages.Delete.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.DeletedEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Receive.Response apply(
    OutsourcingOrderItemMessages.Receive.Request request) {
    if (!this.isReceivable()) {
      throw new OutsourcingOrderItemExceptions.CannotReceiveException();
    }
    this.receivedQuantity = this.receivedQuantity.add(request.getQuantity());
    if (this.receivedQuantity.compareTo(this.quantity) > -1) {
      this.status = OutsourcingOrderItemStatusKind.RECEIVED;
    } else {
      this.status = OutsourcingOrderItemStatusKind.IN_RECEIVING;
    }
    return new OutsourcingOrderItemMessages.Receive.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.ReceivedEvent(this.id,
        request.getQuantity(),
        this.status == OutsourcingOrderItemStatusKind.RECEIVED))
    );
  }

  public OutsourcingOrderItemMessages.Determine.Response apply(
    OutsourcingOrderItemMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new OutsourcingOrderExceptions.CannotDetermineException();
    }
    this.status = OutsourcingOrderItemStatusKind.DETERMINED;
    return new OutsourcingOrderItemMessages.Determine.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.DeterminedEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Cancel.Response apply(
    OutsourcingOrderItemMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new OutsourcingOrderExceptions.CannotCancelException();
    }
    this.status = OutsourcingOrderItemStatusKind.CANCELED;
    return new OutsourcingOrderItemMessages.Cancel.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.CanceledEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Send.Response apply(
    OutsourcingOrderItemMessages.Send.Request request) {
    if (!isSendable()) {
      throw new OutsourcingOrderExceptions.CannotSendException();
    }
    this.status = OutsourcingOrderItemStatusKind.SENT;
    return new OutsourcingOrderItemMessages.Send.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.SentEvent(this.id))
    );
  }

  public OutsourcingOrderItemMessages.Reject.Response apply(
    OutsourcingOrderItemMessages.Reject.Request request) {
    if (!isRejectable()) {
      throw new OutsourcingOrderExceptions.CannotRejectException();
    }
    this.status = OutsourcingOrderItemStatusKind.REJECTED;
    return new OutsourcingOrderItemMessages.Reject.Response(
      Arrays.asList(new OutsourcingOrderItemEvents.RejectedEvent(this.id))
    );
  }

  private OutsourcingOrderItemContext createContext() {
    return OutsourcingOrderItemContextImpl.builder()
      .itemId(itemId)
      .processId(processId)
      .quantity(quantity)
      .build();
  }

  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isDeterminable() {
    return status.isDeterminable();
  }

  public boolean isReceivable() {
    return status.isReceivable();
  }

  public boolean isRejectable() {
    return status.isRejectable();
  }

  public boolean isSendable() {
    return status.isSendable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }
}
