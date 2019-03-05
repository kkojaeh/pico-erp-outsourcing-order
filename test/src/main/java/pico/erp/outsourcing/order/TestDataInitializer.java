package pico.erp.outsourcing.order;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemRequests;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.shared.ApplicationInitializer;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@Profile({"test", "default"})
public class TestDataInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;


  @Autowired
  private DataProperties dataProperties;

  @Override
  public void initialize() {
    dataProperties.outsourcingOrders.forEach(outsourcingOrderService::create);
    dataProperties.outsourcingOrderItems.forEach(outsourcingOrderItemService::create);
  }

  @Data
  @Configuration
  @ConfigurationProperties("data")
  public static class DataProperties {

    List<OutsourcingOrderRequests.CreateRequest> outsourcingOrders = new LinkedList<>();

    List<OutsourcingOrderItemRequests.CreateRequest> outsourcingOrderItems = new LinkedList<>();

  }

}
