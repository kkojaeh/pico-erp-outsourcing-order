package pico.erp.outsourcing.order;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.data.Role;

public final class OutsourcingOrderApi {

  public final static ApplicationId ID = ApplicationId.from("outsourcing-order");

  @RequiredArgsConstructor
  public enum Roles implements Role {

    OUTSOURCING_ORDER_CHARGER,
    OUTSOURCING_ORDER_MANAGER;

    @Id
    @Getter
    private final String id = name();

  }
}
