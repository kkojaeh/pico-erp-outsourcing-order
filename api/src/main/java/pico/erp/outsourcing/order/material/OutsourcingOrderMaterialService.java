package pico.erp.outsourcing.order.material;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.outsourcing.order.OutsourcingOrderId;

public interface OutsourcingOrderMaterialService {

  OutsourcingOrderMaterialData create(
    @Valid @NotNull OutsourcingOrderMaterialRequests.CreateRequest request);

  void delete(@Valid @NotNull OutsourcingOrderMaterialRequests.DeleteRequest request);

  boolean exists(@Valid @NotNull OutsourcingOrderMaterialId id);

  OutsourcingOrderMaterialData get(@Valid @NotNull OutsourcingOrderMaterialId id);

  List<OutsourcingOrderMaterialData> getAll(@Valid @NotNull OutsourcingOrderId orderId);

  void update(@Valid @NotNull OutsourcingOrderMaterialRequests.UpdateRequest request);

}
