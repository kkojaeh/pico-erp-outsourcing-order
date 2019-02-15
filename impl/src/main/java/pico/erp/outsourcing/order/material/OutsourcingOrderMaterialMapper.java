package pico.erp.outsourcing.order.material;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.OutsourcingOrderMapper;
import pico.erp.shared.data.Auditor;

@Mapper
public abstract class OutsourcingOrderMaterialMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  private OutsourcingOrderMaterialRepository outsourcingOrderMaterialRepository;


  @Autowired
  private OutsourcingOrderMapper orderMapper;

  protected OutsourcingOrderMaterialId id(OutsourcingOrderMaterial outsourcingOrderMaterial) {
    return outsourcingOrderMaterial != null ? outsourcingOrderMaterial.getId() : null;
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract OutsourcingOrderMaterialEntity jpa(OutsourcingOrderMaterial data);

  public OutsourcingOrderMaterial jpa(OutsourcingOrderMaterialEntity entity) {
    return OutsourcingOrderMaterial.builder()
      .id(entity.getId())
      .order(map(entity.getOrderId()))
      .itemId(entity.getItemId())
      .itemSpecCode(entity.getItemSpecCode())
      .quantity(entity.getQuantity())
      .unit(entity.getUnit())
      .remark(entity.getRemark())
      .supplierId(entity.getSupplierId())
      .estimatedSupplyDate(entity.getEstimatedSupplyDate())
      .build();
  }

  public OutsourcingOrderMaterial map(OutsourcingOrderMaterialId outsourcingOrderMaterialId) {
    return Optional.ofNullable(outsourcingOrderMaterialId)
      .map(id -> outsourcingOrderMaterialRepository.findBy(id)
        .orElseThrow(OutsourcingOrderMaterialExceptions.NotFoundException::new)
      )
      .orElse(null);
  }


  protected OutsourcingOrder map(OutsourcingOrderId outsourcingOrderId) {
    return orderMapper.map(outsourcingOrderId);
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id")
  })
  public abstract OutsourcingOrderMaterialData map(OutsourcingOrderMaterial item);

  @Mappings({
    @Mapping(target = "order", source = "orderId")
  })
  public abstract OutsourcingOrderMaterialMessages.Create.Request map(
    OutsourcingOrderMaterialRequests.CreateRequest request);

  public abstract OutsourcingOrderMaterialMessages.Update.Request map(
    OutsourcingOrderMaterialRequests.UpdateRequest request);

  public abstract OutsourcingOrderMaterialMessages.Delete.Request map(
    OutsourcingOrderMaterialRequests.DeleteRequest request);


  public abstract void pass(
    OutsourcingOrderMaterialEntity from, @MappingTarget OutsourcingOrderMaterialEntity to);


}



