package pico.erp.outsourcing.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.company.address.CompanyAddressData;
import pico.erp.company.address.CompanyAddressService;
import pico.erp.document.context.DocumentContextFactory;
import pico.erp.document.subject.DocumentSubjectDefinition;
import pico.erp.document.subject.DocumentSubjectId;
import pico.erp.item.ItemService;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.outsourcing.order.material.OutsourcingOrderMaterialService;
import pico.erp.process.ProcessService;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class OutsourcingOrderDraftDocumentSubjectDefinition implements
  DocumentSubjectDefinition<OutsourcingOrderId, Object> {

  public static DocumentSubjectId ID = DocumentSubjectId.from("outsourcing-order-draft");

  @Getter
  DocumentSubjectId id = ID;

  @Getter
  String name = "[outsourcing-order] 외주 발주서";

  @Lazy
  @Autowired
  private DocumentContextFactory contextFactory;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private CompanyAddressService companyAddressService;

  @Lazy
  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Lazy
  @Autowired
  private OutsourcingOrderMaterialService outsourcingOrderMaterialService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Lazy
  @Autowired
  private MessageSource messageSource;

  @Lazy
  @Autowired
  private ProcessService processService;

  @Override
  public Object getContext(OutsourcingOrderId key) {
    val locale = LocaleContextHolder.getLocale();
    val context = contextFactory.factory();
    val data = context.getData();
    val order = outsourcingOrderService.get(key);

    val owner = companyService.getOwner();
    val supplier = companyService.get(order.getSupplierId());
    val receiver = companyService.get(order.getReceiverId());
    val ownerAddress = companyAddressService.getAll(owner.getId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());

    val receiverAddress = companyAddressService.getAll(order.getReceiverId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());

    val supplierAddress = companyAddressService.getAll(order.getSupplierId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());

    val charger = userService.get(order.getChargerId());
    List<Map<String, Object>> items = outsourcingOrderItemService.getAll(key).stream()
      .map(item -> {
        val map = new HashMap<String, Object>();
        val p = processService.get(item.getProcessId());
        map.put("data", item);
        map.put("process", p);
        map.put("properties", p.getDisplayProperties().entrySet());
        map.put("item", itemService.get(item.getItemId()));
        map.put("unitLabel", messageSource
          .getMessage(item.getUnit().getNameCode(), null, item.getUnit().getDefault(),
            locale));
        return map;
      })
      .collect(Collectors.toList());

    List<Map<String, Object>> materials = outsourcingOrderMaterialService.getAll(key).stream()
      .map(item -> {
        val map = new HashMap<String, Object>();
        map.put("data", item);
        map.put("item", itemService.get(item.getItemId()));
        map.put("supplier", companyService.get(item.getSupplierId()));
        map.put("unitLabel", messageSource
          .getMessage(item.getUnit().getNameCode(), null, item.getUnit().getDefault(),
            locale));
        return map;
      })
      .collect(Collectors.toList());

    data.put("owner", owner);
    data.put("supplier", supplier);
    data.put("receiver", receiver);
    data.put("ownerAddress", ownerAddress);
    data.put("receiverAddress", receiverAddress);
    data.put("supplierAddress", supplierAddress);
    data.put("charger", charger);
    data.put("order", order);
    data.put("items", items);
    data.put("materials", materials);
    return context;
  }

  @Override
  public OutsourcingOrderId getKey(String key) {
    return OutsourcingOrderId.from(key);
  }

}
