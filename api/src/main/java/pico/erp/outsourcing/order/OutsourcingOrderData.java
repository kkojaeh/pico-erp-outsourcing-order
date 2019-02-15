package pico.erp.outsourcing.order;

import java.time.OffsetDateTime;
import lombok.Data;
import pico.erp.company.CompanyId;
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

  OffsetDateTime dueDate;

  OffsetDateTime determinedDate;

  OffsetDateTime receivedDate;

  OffsetDateTime sentDate;

  OffsetDateTime rejectedDate;

  OffsetDateTime canceledDate;

  OutsourcingOrderStatusKind status;

  String remark;

  boolean cancelable;

  boolean determinable;

  boolean receivable;

  boolean rejectable;

  boolean sendable;

  boolean updatable;

  boolean printable;

}
