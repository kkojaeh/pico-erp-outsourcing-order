package pico.erp.outsourcing.order.item;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.request.OutsourcingRequestId;

@Repository
interface OutsourcingOrderItemEntityRepository extends
  CrudRepository<OutsourcingOrderItemEntity, OutsourcingOrderItemId> {

  @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM OutsourcingOrderItem i WHERE i.requestId = :requestId")
  boolean exists(@Param("requestId") OutsourcingRequestId requestId);

  @Query("SELECT i FROM OutsourcingOrderItem i WHERE i.orderId = :orderId ORDER BY i.createdDate")
  Stream<OutsourcingOrderItemEntity> findAllBy(@Param("orderId") OutsourcingOrderId orderId);

  @Query("SELECT i FROM OutsourcingOrderItem i WHERE i.requestId = :requestId")
  OutsourcingOrderItemEntity findBy(@Param("requestId") OutsourcingRequestId requestId);

}

@Repository
@Transactional
public class OutsourcingOrderItemRepositoryJpa implements OutsourcingOrderItemRepository {

  @Autowired
  private OutsourcingOrderItemEntityRepository repository;

  @Autowired
  private OutsourcingOrderItemMapper mapper;

  @Override
  public OutsourcingOrderItem create(OutsourcingOrderItem item) {
    val entity = mapper.jpa(item);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(OutsourcingOrderItemId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(OutsourcingOrderItemId id) {
    return repository.existsById(id);
  }

  @Override
  public boolean exists(OutsourcingRequestId requestId) {
    return repository.exists(requestId);
  }

  @Override
  public Stream<OutsourcingOrderItem> findAllBy(OutsourcingOrderId orderId) {
    return repository.findAllBy(orderId)
      .map(mapper::jpa);
  }

  @Override
  public Optional<OutsourcingOrderItem> findBy(OutsourcingOrderItemId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public Optional<OutsourcingOrderItem> findBy(OutsourcingRequestId requestId) {
    return Optional.ofNullable(repository.findBy(requestId))
      .map(mapper::jpa);
  }

  @Override
  public void update(OutsourcingOrderItem planItem) {
    val entity = repository.findById(planItem.getId()).get();
    mapper.pass(mapper.jpa(planItem), entity);
    repository.save(entity);
  }
}
