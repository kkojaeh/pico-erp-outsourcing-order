package pico.erp.outsourcing.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.ContentInputStream;

public interface OutsourcingOrderPrinter {

  ContentInputStream printDraft(OutsourcingOrderId id, DraftPrintOptions options);

  @Data
  @NoArgsConstructor
  class DraftPrintOptions {

  }

}
