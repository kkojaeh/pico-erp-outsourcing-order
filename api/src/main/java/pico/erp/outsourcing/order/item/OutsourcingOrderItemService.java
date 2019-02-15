package pico.erp.outsourcing.order.item;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.request.OutsourcingRequestId;

public interface OutsourcingOrderItemService {

  void cancel(@Valid @NotNull OutsourcingOrderItemRequests.CancelRequest request);

  OutsourcingOrderItemData create(
    @Valid @NotNull OutsourcingOrderItemRequests.CreateRequest request);

  void delete(@Valid @NotNull OutsourcingOrderItemRequests.DeleteRequest request);

  void determine(@Valid @NotNull OutsourcingOrderItemRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull OutsourcingOrderItemId id);

  boolean exists(@Valid @NotNull OutsourcingRequestId requestId);

  void generate(@Valid @NotNull OutsourcingOrderItemRequests.GenerateRequest request);

  OutsourcingOrderItemData get(@Valid @NotNull OutsourcingOrderItemId id);

  OutsourcingOrderItemData get(@Valid @NotNull OutsourcingRequestId requestId);

  List<OutsourcingOrderItemData> getAll(@Valid @NotNull OutsourcingOrderId orderId);

  void receive(@Valid @NotNull OutsourcingOrderItemRequests.ReceiveRequest request);

  void reject(@Valid @NotNull OutsourcingOrderItemRequests.RejectRequest request);

  void send(@Valid @NotNull OutsourcingOrderItemRequests.SendRequest request);

  void update(@Valid @NotNull OutsourcingOrderItemRequests.UpdateRequest request);

}
