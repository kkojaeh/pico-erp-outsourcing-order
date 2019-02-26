package pico.erp.outsourcing.order.material

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.company.CompanyId
import pico.erp.item.ItemId
import pico.erp.item.spec.ItemSpecCode
import pico.erp.outsourcing.order.OutsourcingOrderId
import pico.erp.outsourcing.order.OutsourcingOrderRequests
import pico.erp.outsourcing.order.OutsourcingOrderService
import pico.erp.shared.IntegrationConfiguration
import pico.erp.shared.data.UnitKind
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class OutsourcingOrderMaterialServiceSpec extends Specification {

  @Autowired
  OutsourcingOrderService orderService

  @Autowired
  OutsourcingOrderMaterialService orderMaterialService

  def orderId = OutsourcingOrderId.from("outsourcing-order-1")

  def id = OutsourcingOrderMaterialId.from("outsourcing-order-material-1")

  def unknownId = OutsourcingOrderMaterialId.from("unknown")

  def itemId = ItemId.from("item-2")

  def itemSpecCode = ItemSpecCode.NOT_APPLICABLE

  def unit = UnitKind.EA

  def supplierId = CompanyId.from("SUPP2")

  def setup() {

  }

  def cancelOrder() {
    orderService.cancel(
      new OutsourcingOrderRequests.CancelRequest(
        id: orderId
      )
    )
  }

  def determineOrder() {
    orderService.determine(
      new OutsourcingOrderRequests.DetermineRequest(
        id: orderId
      )
    )
  }

  def rejectOrder() {
    orderService.reject(
      new OutsourcingOrderRequests.RejectRequest(
        id: orderId,
        rejectedReason: "납기를 맞출수 없음"
      )
    )
  }

  def receiveOrder() {
    orderService.receive(
      new OutsourcingOrderRequests.ReceiveRequest(
        id: orderId
      )
    )
  }

  def sendOrder() {
    orderService.send(
      new OutsourcingOrderRequests.SendRequest(
        id: orderId
      )
    )

  }

  def createItem() {
    orderMaterialService.create(
      new OutsourcingOrderMaterialRequests.CreateRequest(
        id: id,
        orderId: orderId,
        itemId: itemId,
        itemSpecCode: itemSpecCode,
        quantity: 100,
        unit: unit,
        remark: "품목 비고",
        supplierId: supplierId
      )
    )
  }

  def createItem2() {
    orderMaterialService.create(
      new OutsourcingOrderMaterialRequests.CreateRequest(
        id: OutsourcingOrderMaterialId.from("outsourcing-order-material-2"),
        orderId: orderId,
        itemId: itemId,
        itemSpecCode: itemSpecCode,
        quantity: 100,
        unit: unit,
        remark: "품목 비고",
        supplierId: supplierId
      )
    )
  }

  def updateItem() {
    orderMaterialService.update(
      new OutsourcingOrderMaterialRequests.UpdateRequest(
        id: id,
        quantity: 200,
        unit: unit,
        remark: "품목 비고2",
        supplierId: supplierId
      )
    )
  }

  def deleteItem() {
    orderMaterialService.delete(
      new OutsourcingOrderMaterialRequests.DeleteRequest(
        id: id
      )
    )
  }


  def "존재 - 아이디로 확인"() {
    when:
    createItem()
    def exists = orderMaterialService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = orderMaterialService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    createItem()
    def item = orderMaterialService.get(id)

    then:
    item.id == id
    item.itemId == itemId
    item.orderId == orderId
    item.quantity == 100
    item.remark == "품목 비고"
    item.unit == unit

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    orderMaterialService.get(unknownId)

    then:
    thrown(OutsourcingOrderMaterialExceptions.NotFoundException)
  }

  def "생성 - 작성 후 생성"() {
    when:
    createItem()
    def items = orderMaterialService.getAll(orderId)
    then:
    items.size() > 0
  }

  def "생성 - 제출 후 생성"() {
    when:
    createItem()
    determineOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)

  }

  def "생성 - 접수 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)
  }

  def "생성 - 진행 중 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)

  }

  def "생성 - 취소 후 생성"() {
    when:
    createItem()
    cancelOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)
  }

  def "생성 - 반려 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)
  }

  def "생성 - 수령 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    createItem2()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotCreateException)
  }

  def "수정 - 작성 후 수정"() {
    when:
    createItem()
    updateItem()
    def items = orderMaterialService.getAll(orderId)
    then:
    items.size() > 0
  }

  def "수정 - 제출 후 수정"() {
    when:
    createItem()
    determineOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)

  }

  def "수정 - 접수 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)
  }

  def "수정 - 진행 중 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)

  }

  def "수정 - 취소 후 수정"() {
    when:
    createItem()
    cancelOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)
  }

  def "수정 - 반려 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)
  }

  def "수정 - 수령 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    updateItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotUpdateException)
  }


  def "삭제 - 작성 후 삭제"() {
    when:
    createItem()
    deleteItem()
    def items = orderMaterialService.getAll(orderId)
    then:
    items.size() == 0
  }

  def "삭제 - 제출 후 삭제"() {
    when:
    createItem()
    determineOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)

  }

  def "삭제 - 접수 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)
  }

  def "삭제 - 진행 중 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)

  }

  def "삭제 - 취소 후 삭제"() {
    when:
    createItem()
    cancelOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)
  }

  def "삭제 - 반려 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)
  }

  def "삭제 - 수령 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    deleteItem()
    then:
    thrown(OutsourcingOrderMaterialExceptions.CannotDeleteException)
  }


}
