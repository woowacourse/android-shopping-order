package woowacourse.shopping.orderdetail

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import woowacourse.shopping.createOrder
import woowacourse.shopping.createOrderModel
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.ui.orderdetail.OrderDetailContract
import woowacourse.shopping.ui.orderdetail.OrderDetailPresenter

class OrderDetailPresenterTest {
    private val view: OrderDetailContract.View = mockk()
    private val memberRepository: MemberRepository = mockk()
    private lateinit var presenter: OrderDetailContract.Presenter

    @Test
    fun `주문 상세를 노출한다`() {
        // given
        presenter = OrderDetailPresenter(view, memberRepository)

        val order = createOrder()
        val successSlot = slot<(Order) -> Unit>()
        every { memberRepository.getOrder(any(), capture(successSlot), any()) } answers {
            successSlot.captured(order)
        }

        every { view.showDetail(any()) } just runs

        // when
        presenter.loadDetail(1)

        // then
        val expected = createOrderModel()
        verify { view.showDetail(expected) }
    }
}