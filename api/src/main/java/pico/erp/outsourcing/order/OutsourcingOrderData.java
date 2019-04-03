package pico.erp.outsourcing.order;

import java.time.LocalDateTime;
import lombok.Data;
import pico.erp.company.CompanyId;
import pico.erp.delivery.DeliveryId;
import pico.erp.document.DocumentId;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

@Data
public class OutsourcingOrderData {

  OutsourcingOrderId id;

  OutsourcingOrderCode code;

  UserId chargerId;

  String rejectedReason;

  CompanyId supplierId;

  CompanyId receiverId;

  Address receiveAddress;

  LocalDateTime dueDate;

  LocalDateTime determinedDate;

  LocalDateTime receivedDate;

  LocalDateTime sentDate;

  LocalDateTime rejectedDate;

  LocalDateTime canceledDate;

  OutsourcingOrderStatusKind status;

  String remark;

  boolean cancelable;

  boolean determinable;

  boolean receivable;

  boolean rejectable;

  boolean sendable;

  boolean updatable;

  boolean printable;

  DocumentId draftId;

  DeliveryId deliveryId;

}
