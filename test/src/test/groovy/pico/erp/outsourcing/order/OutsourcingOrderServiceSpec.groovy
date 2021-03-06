package pico.erp.outsourcing.order

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.company.CompanyId
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import pico.erp.shared.data.Address
import pico.erp.user.UserId
import spock.lang.Specification

import java.time.OffsetDateTime

@SpringBootTest(classes = [OutsourcingOrderApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
class OutsourcingOrderServiceSpec extends Specification {

  @Autowired
  OutsourcingOrderService orderService

  def id = OutsourcingOrderId.from("order-1")

  def unknownId = OutsourcingOrderId.from("unknown")

  def dueDate = OffsetDateTime.now().plusDays(7)

  def remark = "요청 비고"

  def receiverId = CompanyId.from("CUST1")

  def supplierId = CompanyId.from("SUPP1")

  def chargerId = UserId.from("kjh")

  def receiverId2 = CompanyId.from("CUST2")

  def supplierId2 = CompanyId.from("SUPP2")

  def dueDate2 = OffsetDateTime.now().plusDays(8)

  def remark2 = "요청 비고2"

  def receiveAddress = new Address(
    postalCode: '13496',
    street: '경기도 성남시 분당구 장미로 42',
    detail: '야탑리더스 410호'
  )

  def receiveAddress2 = new Address(
    postalCode: '13490',
    street: '경기도 성남시 분당구 장미로 40',
    detail: '야탑리더스 510호'
  )

  def setup() {
    orderService.create(
      new OutsourcingOrderRequests.CreateRequest(
        id: id,
        receiverId: receiverId,
        supplierId: supplierId,
        receiveAddress: receiveAddress,
        chargerId: chargerId,
        dueDate: dueDate,
        remark: remark,
      )
    )
  }

  def cancelOrder() {
    orderService.cancel(
      new OutsourcingOrderRequests.CancelRequest(
        id: id
      )
    )
  }

  def determineOrder() {
    orderService.determine(
      new OutsourcingOrderRequests.DetermineRequest(
        id: id
      )
    )
  }

  def rejectOrder() {
    orderService.reject(
      new OutsourcingOrderRequests.RejectRequest(
        id: id,
        rejectedReason: "작업자가 없어요"
      )
    )
  }

  def receiveOrder() {
    orderService.receive(
      new OutsourcingOrderRequests.ReceiveRequest(
        id: id
      )
    )
  }

  def sendOrder() {
    orderService.send(
      new OutsourcingOrderRequests.SendRequest(
        id: id
      )
    )
  }

  def updateOrder() {
    orderService.update(
      new OutsourcingOrderRequests.UpdateRequest(
        id: id,
        receiverId: receiverId2,
        supplierId: supplierId2,
        receiveAddress: receiveAddress2,
        dueDate: dueDate2,
        remark: remark2,
        chargerId: chargerId
      )
    )
  }


  def "존재 - 아이디로 존재 확인"() {
    when:
    def exists = orderService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = orderService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def order = orderService.get(id)

    then:
    order.id == id
    order.receiverId == receiverId
    order.remark == remark
    order.chargerId == chargerId
    order.dueDate == dueDate
    order.receiveAddress == receiveAddress

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    orderService.get(unknownId)

    then:
    thrown(OutsourcingOrderExceptions.NotFoundException)
  }


  def "수정 - 취소 후 수정"() {
    when:
    cancelOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }

  def "수정 - 전송 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }

  def "수정 - 확정 후 수정"() {
    when:
    determineOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }

  def "수정 - 수령 후 수정"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }

  def "수정 - 반려 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }

  def "수정 - 진행 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    updateOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotUpdateException)
  }


  def "수정 - 작성 후 수정"() {
    when:
    updateOrder()
    def order = orderService.get(id)

    then:
    order.receiverId == receiverId2
    order.supplierId == supplierId2
    order.receiveAddress == receiveAddress2
    order.dueDate == dueDate2
    order.remark == remark2
    order.chargerId == chargerId
  }

  def "확정 - 작성 후 확정"() {
    when:
    determineOrder()
    def order = orderService.get(id)
    then:
    order.status == OutsourcingOrderStatusKind.DETERMINED
  }

  def "확정 - 확정 후 확정"() {
    when:
    determineOrder()
    determineOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotDetermineException)
  }


  def "확정 - 취소 후 확정"() {
    when:
    cancelOrder()
    determineOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotDetermineException)
  }

  def "확정 - 반려 후 확정"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    determineOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotDetermineException)
  }

  def "확정 - 수령 후 확정"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    determineOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotDetermineException)
  }

  def "전송 - 작성 후 전송"() {
    when:
    sendOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotSendException)
  }

  def "전송 - 전송 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    sendOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotSendException)
  }

  def "전송 - 확정 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    def order = orderService.get(id)
    then:
    order.status == OutsourcingOrderStatusKind.SENT
  }

  def "전송 - 취소 후 전송"() {
    when:
    cancelOrder()
    sendOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotSendException)
  }

  def "전송 - 반려 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    sendOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotSendException)
  }

  def "전송 - 수령 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    sendOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotSendException)
  }


  def "취소 - 취소 후에는 취소"() {
    when:
    cancelOrder()
    cancelOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotCancelException)
  }

  def "취소 - 확정 후 취소"() {
    when:
    determineOrder()
    cancelOrder()
    def request = orderService.get(id)
    then:
    request.status == OutsourcingOrderStatusKind.CANCELED
  }

  def "취소 - 전송 후 취소"() {
    when:
    determineOrder()
    sendOrder()
    cancelOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotCancelException)
  }

  def "취소 - 진행 후 취소"() {
    when:
    determineOrder()
    sendOrder()

    cancelOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotCancelException)
  }

  def "취소 - 반려 후 취소"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    cancelOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotCancelException)
  }

  def "취소 - 수령 후 취소"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    cancelOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotCancelException)
  }

  def "반려 - 작성 후 반려"() {
    when:
    rejectOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotRejectException)
  }

  def "반려 - 확정 후 반려"() {
    when:
    determineOrder()
    rejectOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotRejectException)

  }

  def "반려 - 전송 후 반려"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    def order = orderService.get(id)
    then:

    order.status == OutsourcingOrderStatusKind.REJECTED
  }


  def "반려 - 취소 후 반려"() {
    when:
    cancelOrder()
    rejectOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotRejectException)
  }

  def "반려 - 반려 후 반려"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    rejectOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotRejectException)
  }

  def "반려 - 수령 후 반려"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    rejectOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotRejectException)
  }

  def "수령 - 작성 후 수령"() {
    when:
    receiveOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotReceiveException)
  }

  def "수령 - 확정 후 수령"() {
    when:
    determineOrder()
    receiveOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotReceiveException)

  }

  def "수령 - 전송 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    def order = orderService.get(id)
    then:
    order.status == OutsourcingOrderStatusKind.RECEIVED
  }

  def "수령 - 취소 후 수령"() {
    when:
    cancelOrder()
    receiveOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotReceiveException)
  }

  def "수령 - 반려 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    receiveOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotReceiveException)
  }

  def "수령 - 수령 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    receiveOrder()
    then:
    thrown(OutsourcingOrderExceptions.CannotReceiveException)
  }


}
