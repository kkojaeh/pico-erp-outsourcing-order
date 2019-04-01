package pico.erp.config;

import java.math.BigDecimal;
import kkojaeh.spring.boot.component.Give;
import kkojaeh.spring.boot.component.Take;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator;
import pico.erp.process.ProcessService;

@Configuration
public class OutsourcingOrderConfiguration {


  @Give
  @Bean
  @ConditionalOnMissingBean(OutsourcingOrderItemUnitCostEstimator.class)
  public OutsourcingOrderItemUnitCostEstimator defaultOutsourcingOrderItemUnitCostEstimator() {
    return new DefaultOutsourcingOrderItemUnitCostEstimator();
  }

  public static class DefaultOutsourcingOrderItemUnitCostEstimator implements
    OutsourcingOrderItemUnitCostEstimator {

    @Take
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
