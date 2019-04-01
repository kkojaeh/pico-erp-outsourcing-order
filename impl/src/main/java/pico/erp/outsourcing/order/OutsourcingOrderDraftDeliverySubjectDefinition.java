package pico.erp.outsourcing.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kkojaeh.spring.boot.component.Give;
import kkojaeh.spring.boot.component.Take;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.delivery.subject.DeliverySubjectDefinition;
import pico.erp.delivery.subject.DeliverySubjectId;
import pico.erp.document.DocumentService;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.user.UserService;

@Give
@Component
public class OutsourcingOrderDraftDeliverySubjectDefinition implements
  DeliverySubjectDefinition<OutsourcingOrderId, Object> {

  public static DeliverySubjectId ID = DeliverySubjectId.from("outsourcing-order-draft");

  @Getter
  DeliverySubjectId id = ID;

  @Getter
  String name = "[outsourcing-order] 외주 발주서";

  @Take
  private CompanyService companyService;

  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Take
  private UserService userService;

  @Take
  private DocumentService documentService;

  @Override
  public List<ContentInputStream> getAttachments(OutsourcingOrderId key) {
    val order = outsourcingOrderService.get(key);
    return Arrays.asList(documentService.load(order.getDraftId()));
  }

  @Override
  public Object getContext(OutsourcingOrderId key) {
    val data = new HashMap<String, Object>();
    val order = outsourcingOrderService.get(key);
    data.put("supplier", companyService.get(order.getSupplierId()));
    data.put("owner", companyService.getOwner());
    data.put("charger", userService.get(order.getChargerId()));
    data.put("order", order);
    return data;
  }

  @Override
  public OutsourcingOrderId toKey(String key) {
    return OutsourcingOrderId.from(key);
  }

  @Override
  public String toString(OutsourcingOrderId key) {
    return key.getValue().toString();
  }

}
