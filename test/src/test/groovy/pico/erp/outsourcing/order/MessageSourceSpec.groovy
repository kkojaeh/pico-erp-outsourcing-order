package pico.erp.outsourcing.order

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.bom.BomApplication
import pico.erp.company.CompanyApplication
import pico.erp.delivery.DeliveryApplication
import pico.erp.document.DocumentApplication
import pico.erp.item.ItemApplication
import pico.erp.outsourcing.order.item.OutsourcingOrderItemStatusKind
import pico.erp.outsourcing.request.OutsourcingRequestApplication
import pico.erp.process.ProcessApplication
import pico.erp.project.ProjectApplication
import pico.erp.shared.TestParentApplication
import pico.erp.user.UserApplication
import pico.erp.warehouse.WarehouseApplication
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

@SpringBootTest(classes = [OutsourcingOrderApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [
  UserApplication, ItemApplication, ProjectApplication, ProcessApplication, CompanyApplication,
  DocumentApplication, DeliveryApplication, WarehouseApplication, OutsourcingRequestApplication,
  BomApplication
])
@Transactional
@Rollback
@ActiveProfiles("test")
class MessageSourceSpec extends Specification {

  @Autowired
  MessageSource messageSource

  def locale = LocaleContextHolder.locale

  def "발주 상태"() {
    when:
    def messages = Stream.of(OutsourcingOrderStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 6
  }

  def "발주 품목 상태"() {
    when:
    def messages = Stream.of(OutsourcingOrderItemStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 7
  }


}
