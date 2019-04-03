package pico.erp.outsourcing.order;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.data.Role;

public final class OutsourcingOrderApi {

  @RequiredArgsConstructor
  public enum Roles implements Role {

    OUTSOURCING_ORDER_CHARGER,

    OUTSOURCING_ORDER_MANAGER;

    @Id
    @Getter
    private final String id = name();

  }
}
