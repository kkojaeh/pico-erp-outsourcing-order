package pico.erp.outsourcing.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pico.erp.shared.ApplicationInitializer;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class OutsourcingOrderInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  GroupService groupService;

  @Autowired
  OutsourcingOrderProperties properties;

  @Override
  public void initialize() {
    val chargerGroup = properties.getChargerGroup();
    if (!groupService.exists(chargerGroup.getId())) {
      groupService.create(
        GroupRequests.CreateRequest.builder()
          .id(chargerGroup.getId())
          .name(chargerGroup.getName())
          .build()
      );
    }
  }
}
