package pico.erp.outsourcing.order;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pico.erp.shared.Public;
import pico.erp.user.group.GroupData;

@Public
@Data
@Configuration
@ConfigurationProperties("outsourcing-order")
public class OutsourcingOrderPropertiesImpl implements OutsourcingOrderProperties {

  GroupData chargerGroup;

}
