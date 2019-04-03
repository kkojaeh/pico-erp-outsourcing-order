package pico.erp.outsourcing.order.material;

import java.util.List;
import java.util.stream.Collectors;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
public class OutsourcingOrderMaterialServiceLogic implements OutsourcingOrderMaterialService {

  @Autowired
  private OutsourcingOrderMaterialRepository outsourcingOrderMaterialRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private OutsourcingOrderMaterialMapper mapper;

  @Override
  public OutsourcingOrderMaterialData create(
    OutsourcingOrderMaterialRequests.CreateRequest request) {
    val material = new OutsourcingOrderMaterial();
    val response = material.apply(mapper.map(request));
    if (outsourcingOrderMaterialRepository.exists(material.getId())) {
      throw new OutsourcingOrderMaterialExceptions.AlreadyExistsException();
    }
    val created = outsourcingOrderMaterialRepository.create(material);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(OutsourcingOrderMaterialRequests.DeleteRequest request) {
    val material = outsourcingOrderMaterialRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderMaterialExceptions.NotFoundException::new);
    val response = material.apply(mapper.map(request));
    outsourcingOrderMaterialRepository.deleteBy(material.getId());
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(OutsourcingOrderMaterialId id) {
    return outsourcingOrderMaterialRepository.exists(id);
  }

  @Override
  public OutsourcingOrderMaterialData get(OutsourcingOrderMaterialId id) {
    return outsourcingOrderMaterialRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(OutsourcingOrderMaterialExceptions.NotFoundException::new);
  }

  @Override
  public List<OutsourcingOrderMaterialData> getAll(OutsourcingOrderId requestId) {
    return outsourcingOrderMaterialRepository.findAllBy(requestId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void update(OutsourcingOrderMaterialRequests.UpdateRequest request) {
    val material = outsourcingOrderMaterialRepository.findBy(request.getId())
      .orElseThrow(OutsourcingOrderMaterialExceptions.NotFoundException::new);
    val response = material.apply(mapper.map(request));
    outsourcingOrderMaterialRepository.update(material);
    eventPublisher.publishEvents(response.getEvents());
  }

}
