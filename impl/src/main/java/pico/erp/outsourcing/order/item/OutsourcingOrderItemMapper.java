package pico.erp.outsourcing.order.item;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.lot.ItemLotData;
import pico.erp.item.lot.ItemLotId;
import pico.erp.item.lot.ItemLotService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.order.OutsourcingOrder;
import pico.erp.outsourcing.order.OutsourcingOrderExceptions;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.OutsourcingOrderMapper;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.shared.data.Auditor;

@Mapper
public abstract class OutsourcingOrderItemMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemLotService itemLotService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  protected OutsourcingOrderItemUnitCostEstimator unitCostEstimator;

  @Lazy
  @Autowired
  private OutsourcingOrderItemRepository outsourcingOrderItemRepository;

  @Autowired
  private OutsourcingOrderMapper orderMapper;

  @Lazy
  @Autowired
  private ProjectService projectService;

  protected OutsourcingOrderItemId id(OutsourcingOrderItem outsourcingOrderItem) {
    return outsourcingOrderItem != null ? outsourcingOrderItem.getId() : null;
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract OutsourcingOrderItemEntity jpa(OutsourcingOrderItem data);

  public OutsourcingOrderItem jpa(OutsourcingOrderItemEntity entity) {
    return OutsourcingOrderItem.builder()
      .id(entity.getId())
      .order(map(entity.getOrderId()))
      .itemId(entity.getItemId())
      .processId(entity.getProcessId())
      .itemSpecCode(entity.getItemSpecCode())
      .quantity(entity.getQuantity())
      .spareQuantity(entity.getSpareQuantity())
      .receivedQuantity(entity.getReceivedQuantity())
      .estimatedUnitCost(entity.getEstimatedUnitCost())
      .unitCost(entity.getUnitCost())
      .unit(entity.getUnit())
      .remark(entity.getRemark())
      .projectId(entity.getProjectId())
      .requestId(entity.getRequestId())
      .status(entity.getStatus())
      .build();
  }

  public OutsourcingOrderItem map(OutsourcingOrderItemId outsourcingOrderItemId) {
    return Optional.ofNullable(outsourcingOrderItemId)
      .map(id -> outsourcingOrderItemRepository.findBy(id)
        .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemLotData map(ItemLotId itemLotId) {
    return Optional.ofNullable(itemLotId)
      .map(itemLotService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  protected OutsourcingOrder map(OutsourcingOrderId outsourcingOrderId) {
    return orderMapper.map(outsourcingOrderId);
  }

  protected ProjectData map(ProjectId projectId) {
    return Optional.ofNullable(projectId)
      .map(projectService::get)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id")
  })
  public abstract OutsourcingOrderItemData map(OutsourcingOrderItem item);

  @Mappings({
    @Mapping(target = "order", source = "orderId"),
    @Mapping(target = "unitCostEstimator", expression = "java(unitCostEstimator)")
  })
  public abstract OutsourcingOrderItemMessages.Create.Request map(
    OutsourcingOrderItemRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "unitCostEstimator", expression = "java(unitCostEstimator)")
  })
  public abstract OutsourcingOrderItemMessages.Update.Request map(
    OutsourcingOrderItemRequests.UpdateRequest request);

  public abstract OutsourcingOrderItemMessages.Delete.Request map(
    OutsourcingOrderItemRequests.DeleteRequest request);

  public abstract OutsourcingOrderItemMessages.Receive.Request map(
    OutsourcingOrderItemRequests.ReceiveRequest request);

  public abstract OutsourcingOrderItemMessages.Determine.Request map(
    OutsourcingOrderItemRequests.DetermineRequest request);

  public abstract OutsourcingOrderItemMessages.Send.Request map(
    OutsourcingOrderItemRequests.SendRequest request);

  public abstract OutsourcingOrderItemMessages.Reject.Request map(
    OutsourcingOrderItemRequests.RejectRequest request);

  public abstract OutsourcingOrderItemMessages.Cancel.Request map(
    OutsourcingOrderItemRequests.CancelRequest request);


  public abstract void pass(
    OutsourcingOrderItemEntity from, @MappingTarget OutsourcingOrderItemEntity to);


}



