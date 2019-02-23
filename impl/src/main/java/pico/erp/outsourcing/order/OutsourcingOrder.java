package pico.erp.outsourcing.order;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.company.CompanyId;
import pico.erp.delivery.DeliveryId;
import pico.erp.document.DocumentId;
import pico.erp.outsourcing.order.OutsourcingOrderEvents.DeterminedEvent;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutsourcingOrder implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  OutsourcingOrderId id;

  OutsourcingOrderCode code;

  OffsetDateTime dueDate;

  CompanyId supplierId;

  CompanyId receiverId;

  Address receiveAddress;

  String remark;

  UserId chargerId;

  OffsetDateTime determinedDate;

  OffsetDateTime receivedDate;

  OffsetDateTime sentDate;

  OffsetDateTime rejectedDate;

  OffsetDateTime canceledDate;

  OutsourcingOrderStatusKind status;

  String rejectedReason;

  DocumentId draftId;

  DeliveryId deliveryId;

  public OutsourcingOrder() {

  }

  public OutsourcingOrderMessages.Create.Response apply(
    OutsourcingOrderMessages.Create.Request request) {
    this.id = request.getId();
    this.dueDate = request.getDueDate();
    this.supplierId = request.getSupplierId();
    this.receiverId = request.getReceiverId();
    this.receiveAddress = request.getReceiveAddress();
    this.remark = request.getRemark();
    this.status = OutsourcingOrderStatusKind.DRAFT;
    this.chargerId = request.getChargerId();
    this.code = request.getCodeGenerator().generate(this);
    return new OutsourcingOrderMessages.Create.Response(
      Arrays.asList(new OutsourcingOrderEvents.CreatedEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Update.Response apply(
    OutsourcingOrderMessages.Update.Request request) {
    if (!isUpdatable()) {
      throw new OutsourcingOrderExceptions.CannotUpdateException();
    }
    this.dueDate = request.getDueDate();
    this.supplierId = request.getSupplierId();
    this.receiverId = request.getReceiverId();
    this.receiveAddress = request.getReceiveAddress();
    this.chargerId = request.getChargerId();
    this.remark = request.getRemark();
    return new OutsourcingOrderMessages.Update.Response(
      Arrays.asList(new OutsourcingOrderEvents.UpdatedEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Determine.Response apply(
    OutsourcingOrderMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new OutsourcingOrderExceptions.CannotDetermineException();
    }
    this.status = OutsourcingOrderStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new OutsourcingOrderMessages.Determine.Response(
      Arrays.asList(new DeterminedEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Cancel.Response apply(
    OutsourcingOrderMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new OutsourcingOrderExceptions.CannotCancelException();
    }
    this.status = OutsourcingOrderStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new OutsourcingOrderMessages.Cancel.Response(
      Arrays.asList(new OutsourcingOrderEvents.CanceledEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Receive.Response apply(
    OutsourcingOrderMessages.Receive.Request request) {
    if (!isReceivable()) {
      throw new OutsourcingOrderExceptions.CannotReceiveException();
    }
    this.status = OutsourcingOrderStatusKind.RECEIVED;
    this.receivedDate = OffsetDateTime.now();
    return new OutsourcingOrderMessages.Receive.Response(
      Arrays.asList(new OutsourcingOrderEvents.ReceivedEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Send.Response apply(
    OutsourcingOrderMessages.Send.Request request) {
    if (!isSendable()) {
      throw new OutsourcingOrderExceptions.CannotSendException();
    }
    this.status = OutsourcingOrderStatusKind.SENT;
    this.sentDate = OffsetDateTime.now();
    return new OutsourcingOrderMessages.Send.Response(
      Arrays.asList(new OutsourcingOrderEvents.SentEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.Reject.Response apply(
    OutsourcingOrderMessages.Reject.Request request) {
    if (!isRejectable()) {
      throw new OutsourcingOrderExceptions.CannotRejectException();
    }
    this.status = OutsourcingOrderStatusKind.REJECTED;
    this.rejectedDate = OffsetDateTime.now();
    this.rejectedReason = request.getRejectedReason();
    return new OutsourcingOrderMessages.Reject.Response(
      Arrays.asList(new OutsourcingOrderEvents.RejectedEvent(this.id))
    );
  }

  public OutsourcingOrderMessages.PrepareSend.Response apply(
    OutsourcingOrderMessages.PrepareSend.Request request) {
    if (!isSendPreparable()) {
      throw new OutsourcingOrderExceptions.CannotPrepareSendException();
    }
    this.draftId = request.getDraftId();
    this.deliveryId = request.getDeliveryId();
    this.status = OutsourcingOrderStatusKind.SEND_PREPARED;
    return new OutsourcingOrderMessages.PrepareSend.Response(
      Collections.emptyList()
    );
  }


  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isDeterminable() {
    return status.isDeterminable();
  }

  public boolean isPrintable() {
    return status.isPrintable();
  }

  public boolean isReceivable() {
    return status.isReceivable();
  }

  public boolean isRejectable() {
    return status.isRejectable();
  }

  public boolean isSendPreparable() {
    return status.isSendPreparable();
  }

  public boolean isSendable() {
    return status.isSendable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }


}
