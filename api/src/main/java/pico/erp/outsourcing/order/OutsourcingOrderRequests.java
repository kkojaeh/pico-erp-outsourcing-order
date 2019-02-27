package pico.erp.outsourcing.order;

import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

public interface OutsourcingOrderRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

    @Future
    @NotNull
    OffsetDateTime dueDate;

    @Valid
    @NotNull
    CompanyId receiverId;

    @Valid
    CompanyId supplierId;

    @Valid
    @NotNull
    Address receiveAddress;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    UserId chargerId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

    @Future
    @NotNull
    OffsetDateTime dueDate;

    @Valid
    @NotNull
    CompanyId receiverId;

    @Valid
    @NotNull
    CompanyId supplierId;

    @Valid
    @NotNull
    Address receiveAddress;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    UserId chargerId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DetermineRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class SendRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class ReceiveRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class RejectRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String rejectedReason;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CancelRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class GenerateRequest {

    @Valid
    @NotNull
    OutsourcingOrderId id;

    @Valid
    @NotNull
    UserId chargerId;

    @Size(min = 1)
    @NotNull
    List<OutsourcingRequestId> requestIds;

  }

}
