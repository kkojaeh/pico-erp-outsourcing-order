package pico.erp.outsourcing.order.material;

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

@Repository
interface OutsourcingOrderMaterialEntityRepository extends
  CrudRepository<OutsourcingOrderMaterialEntity, OutsourcingOrderMaterialId> {

  @Query("SELECT m FROM OutsourcingOrderMaterial m WHERE m.orderId = :orderId ORDER BY m.createdDate")
  Stream<OutsourcingOrderMaterialEntity> findAllBy(
    @Param("orderId") OutsourcingOrderId orderId);

}

@Repository
@Transactional
public class OutsourcingOrderMaterialRepositoryJpa implements
  OutsourcingOrderMaterialRepository {

  @Autowired
  private OutsourcingOrderMaterialEntityRepository repository;

  @Autowired
  private OutsourcingOrderMaterialMapper mapper;

  @Override
  public OutsourcingOrderMaterial create(OutsourcingOrderMaterial planItem) {
    val entity = mapper.jpa(planItem);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(OutsourcingOrderMaterialId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(OutsourcingOrderMaterialId id) {
    return repository.existsById(id);
  }

  @Override
  public Stream<OutsourcingOrderMaterial> findAllBy(OutsourcingOrderId orderId) {
    return repository.findAllBy(orderId)
      .map(mapper::jpa);
  }

  @Override
  public Optional<OutsourcingOrderMaterial> findBy(OutsourcingOrderMaterialId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public void update(OutsourcingOrderMaterial material) {
    val entity = repository.findById(material.getId()).get();
    mapper.pass(mapper.jpa(material), entity);
    repository.save(entity);
  }
}
