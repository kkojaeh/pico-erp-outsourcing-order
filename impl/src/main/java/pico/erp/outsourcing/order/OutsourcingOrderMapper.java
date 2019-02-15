package pico.erp.outsourcing.order;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.company.CompanyData;
import pico.erp.company.CompanyId;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.DetermineRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.ReceiveRequest;
import pico.erp.outsourcing.order.OutsourcingOrderRequests.SendRequest;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserData;
import pico.erp.user.UserId;
import pico.erp.user.UserService;

@Mapper
public abstract class OutsourcingOrderMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Autowired
  protected OutsourcingOrderCodeGenerator outsourcingOrderCodeGenerator;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private OutsourcingOrderRepository outsourcingOrderRepository;

  @Lazy
  @Autowired
  private ProjectService projectService;

  protected Auditor auditor(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::getAuditor)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "receiverId", source = "receiver.id"),
    @Mapping(target = "supplierId", source = "supplier.id"),
    @Mapping(target = "chargerId", source = "charger.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract OutsourcingOrderEntity jpa(OutsourcingOrder data);

  public OutsourcingOrder jpa(OutsourcingOrderEntity entity) {
    return OutsourcingOrder.builder()
      .id(entity.getId())
      .code(entity.getCode())
      .dueDate(entity.getDueDate())
      .supplier(map(entity.getSupplierId()))
      .receiver(map(entity.getReceiverId()))
      .receiveAddress(entity.getReceiveAddress())
      .remark(entity.getRemark())
      .charger(map(entity.getChargerId()))
      .determinedDate(entity.getDeterminedDate())
      .receivedDate(entity.getReceivedDate())
      .sentDate(entity.getSentDate())
      .rejectedDate(entity.getRejectedDate())
      .canceledDate(entity.getCanceledDate())
      .status(entity.getStatus())
      .rejectedReason(entity.getRejectedReason())
      .build();
  }

  protected UserData map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::get)
      .orElse(null);
  }

  protected CompanyData map(CompanyId companyId) {
    return Optional.ofNullable(companyId)
      .map(companyService::get)
      .orElse(null);
  }

  protected ProjectData map(ProjectId projectId) {
    return Optional.ofNullable(projectId)
      .map(projectService::get)
      .orElse(null);
  }

  public OutsourcingOrder map(OutsourcingOrderId outsourcingOrderId) {
    return Optional.ofNullable(outsourcingOrderId)
      .map(id -> outsourcingOrderRepository.findBy(id)
        .orElseThrow(OutsourcingOrderExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "supplierId", source = "supplier.id"),
    @Mapping(target = "receiverId", source = "receiver.id"),
    @Mapping(target = "chargerId", source = "charger.id")
  })
  public abstract OutsourcingOrderData map(OutsourcingOrder outsourcingOrder);

  @Mappings({
    @Mapping(target = "supplier", source = "supplierId"),
    @Mapping(target = "receiver", source = "receiverId"),
    @Mapping(target = "charger", source = "chargerId"),
    @Mapping(target = "codeGenerator", expression = "java(outsourcingOrderCodeGenerator)")
  })
  public abstract OutsourcingOrderMessages.Create.Request map(
    OutsourcingOrderRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "supplier", source = "supplierId"),
    @Mapping(target = "receiver", source = "receiverId"),
    @Mapping(target = "charger", source = "chargerId")
  })
  public abstract OutsourcingOrderMessages.Update.Request map(
    OutsourcingOrderRequests.UpdateRequest request);

  public abstract OutsourcingOrderMessages.Determine.Request map(
    DetermineRequest request);

  public abstract OutsourcingOrderMessages.Send.Request map(
    SendRequest request);

  public abstract OutsourcingOrderMessages.Receive.Request map(
    ReceiveRequest request);

  public abstract OutsourcingOrderMessages.Cancel.Request map(
    OutsourcingOrderRequests.CancelRequest request);

  public abstract OutsourcingOrderMessages.Reject.Request map(
    OutsourcingOrderRequests.RejectRequest request);


  public abstract void pass(OutsourcingOrderEntity from, @MappingTarget OutsourcingOrderEntity to);


}


