package pico.erp.outsourcing.order;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.shared.data.ContentInputStream;

public interface OutsourcingOrderService {

  void cancel(@Valid @NotNull OutsourcingOrderRequests.CancelRequest request);

  OutsourcingOrderData create(@Valid @NotNull OutsourcingOrderRequests.CreateRequest request);

  void determine(@Valid @NotNull OutsourcingOrderRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull OutsourcingOrderId id);

  OutsourcingOrderData generate(@Valid @NotNull OutsourcingOrderRequests.GenerateRequest request);

  OutsourcingOrderData get(@Valid @NotNull OutsourcingOrderId id);

  ContentInputStream printDraft(@Valid @NotNull OutsourcingOrderRequests.PrintDraftRequest request);

  void receive(@Valid @NotNull OutsourcingOrderRequests.ReceiveRequest request);

  void reject(@Valid @NotNull OutsourcingOrderRequests.RejectRequest request);

  void send(@Valid @NotNull OutsourcingOrderRequests.SendRequest request);

  void update(@Valid @NotNull OutsourcingOrderRequests.UpdateRequest request);

}
