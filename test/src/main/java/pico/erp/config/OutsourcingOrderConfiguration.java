package pico.erp.config;

import java.math.BigDecimal;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator;
import pico.erp.process.ProcessService;

@Configuration
public class OutsourcingOrderConfiguration {


  @ComponentBean(host = false)
  @Bean
  @ConditionalOnMissingBean(OutsourcingOrderItemUnitCostEstimator.class)
  public OutsourcingOrderItemUnitCostEstimator defaultOutsourcingOrderItemUnitCostEstimator() {
    return new DefaultOutsourcingOrderItemUnitCostEstimator();
  }

  public static class DefaultOutsourcingOrderItemUnitCostEstimator implements
    OutsourcingOrderItemUnitCostEstimator {

    @ComponentAutowired
    ProcessService processService;

    @Override
    public BigDecimal estimate(OutsourcingOrderItemContext context) {
      if (context.getProcessId() != null) {
        val process = processService.get(context.getProcessId());
        return process.getEstimatedCost().getTotal();
      }
      return null;
    }

  }

}
