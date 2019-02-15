package pico.erp.outsourcing.order.material;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.outsourcing.order.OutsourcingOrderId;

@Repository
public interface OutsourcingOrderMaterialRepository {

  OutsourcingOrderMaterial create(@NotNull OutsourcingOrderMaterial item);

  void deleteBy(@NotNull OutsourcingOrderMaterialId id);

  boolean exists(@NotNull OutsourcingOrderMaterialId id);

  Stream<OutsourcingOrderMaterial> findAllBy(@NotNull OutsourcingOrderId orderId);

  Optional<OutsourcingOrderMaterial> findBy(@NotNull OutsourcingOrderMaterialId id);

  void update(@NotNull OutsourcingOrderMaterial item);

}
