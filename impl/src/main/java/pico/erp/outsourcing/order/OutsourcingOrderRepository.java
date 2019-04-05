package pico.erp.outsourcing.order;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OutsourcingOrderRepository {

  long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end);

  OutsourcingOrder create(@NotNull OutsourcingOrder orderAcceptance);

  void deleteBy(@NotNull OutsourcingOrderId id);

  boolean exists(@NotNull OutsourcingOrderId id);

  Optional<OutsourcingOrder> findBy(@NotNull OutsourcingOrderId id);

  void update(@NotNull OutsourcingOrder orderAcceptance);

}
