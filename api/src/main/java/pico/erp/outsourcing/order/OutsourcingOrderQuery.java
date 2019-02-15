package pico.erp.outsourcing.order;

import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutsourcingOrderQuery {

  Page<OutsourcingOrderView> retrieve(@NotNull OutsourcingOrderView.Filter filter,
    @NotNull Pageable pageable);

}
