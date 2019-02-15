package pico.erp.outsourcing.order.item;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.request.OutsourcingRequestId;

@Repository
public interface OutsourcingOrderItemRepository {

  OutsourcingOrderItem create(@NotNull OutsourcingOrderItem item);

  void deleteBy(@NotNull OutsourcingOrderItemId id);

  boolean exists(@NotNull OutsourcingOrderItemId id);

  boolean exists(@NotNull OutsourcingRequestId requestId);

  Stream<OutsourcingOrderItem> findAllBy(@NotNull OutsourcingOrderId orderId);

  Optional<OutsourcingOrderItem> findBy(@NotNull OutsourcingOrderItemId id);

  Optional<OutsourcingOrderItem> findBy(@NotNull OutsourcingRequestId requestId);

  void update(@NotNull OutsourcingOrderItem item);

}
