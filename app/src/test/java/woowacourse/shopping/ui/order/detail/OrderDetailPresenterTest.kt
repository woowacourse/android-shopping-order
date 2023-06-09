package woowacourse.shopping.ui.order.detail

import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.repository.OrderProductRepository

internal class OrderDetailPresenterTest {
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var view: OrderDetailContract.View
    private lateinit var orderRepository: OrderProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderDetailPresenter(view, 13, orderRepository)
    }

    @Test
    fun 주문_상세_정보를_확인할_수_있다() {
        // given
        every { view.showOrderDetailProducts(any()) } just runs
        every { view.showOrderDetailPaymentInfo(any()) } just runs
        every {
            orderRepository.requestSpecificOrder(any(), any(), any())
        } answers {
            val callback = args[1] as (OrderResponse) -> Unit
            callback(OrderResponse(1, listOf(), Payment(100, 80, 20)))
        }

        // when
        presenter.loadOrderDetailInfo()

        // then
        verify(exactly = 1) { view.showOrderDetailProducts(any()) }
        verify(exactly = 1) { view.showOrderDetailPaymentInfo(any()) }
    }

    @Test
    fun 사용자가_뒤로가기를_누르면_메인_화면으로_돌아간다() {
        // given
        justRun { view.navigateToHome() }

        // when
        presenter.navigateToHome(android.R.id.home)

        // then
        verify(exactly = 1) { view.navigateToHome() }
    }
}
