package pico.erp.outsourcing.order;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface OutsourcingOrderEntityRepository extends
  CrudRepository<OutsourcingOrderEntity, OutsourcingOrderId> {

  @Query("SELECT COUNT(pr) FROM OutsourcingOrder pr WHERE pr.createdDate >= :begin AND pr.createdDate <= :end")
  long countCreatedBetween(@Param("begin") OffsetDateTime begin, @Param("end") OffsetDateTime end);

}

@Repository
@Transactional
public class OutsourcingOrderRepositoryJpa implements OutsourcingOrderRepository {

  @Autowired
  private OutsourcingOrderEntityRepository repository;

  @Autowired
  private OutsourcingOrderMapper mapper;

  @Override
  public long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end) {
    return repository.countCreatedBetween(begin, end);
  }

  @Override
  public OutsourcingOrder create(OutsourcingOrder plan) {
    val entity = mapper.jpa(plan);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(OutsourcingOrderId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(OutsourcingOrderId id) {
    return repository.existsById(id);
  }

  @Override
  public Optional<OutsourcingOrder> findBy(OutsourcingOrderId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public void update(OutsourcingOrder plan) {
    val entity = repository.findById(plan.getId()).get();
    mapper.pass(mapper.jpa(plan), entity);
    repository.save(entity);
  }
}
