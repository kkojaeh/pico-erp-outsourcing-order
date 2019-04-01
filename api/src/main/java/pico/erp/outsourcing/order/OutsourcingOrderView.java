package pico.erp.outsourcing.order;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

@Data
public class OutsourcingOrderView {

  OutsourcingOrderId id;

  OutsourcingOrderCode code;

  UserId chargerId;

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

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String code;

    CompanyId receiverId;

    CompanyId supplierId;

    UserId chargerId;

    ProjectId projectId;

    ItemId itemId;

    Set<OutsourcingOrderStatusKind> statuses;

    LocalDateTime startDueDate;

    LocalDateTime endDueDate;

  }

}
