package woowacourse.shopping.ui.order.main

import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.model.UiCartProduct

internal class OrderPresenterTest {
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var view: OrderContract.View
    private lateinit var cartProducts: List<UiCartProduct>
    private lateinit var orderRepository: OrderProductRepository
    private lateinit var pointRepository: PointRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartProducts = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        pointRepository = mockk(relaxed = true)
        presenter = OrderPresenter(view, cartProducts, orderRepository, pointRepository)
    }

    @Test
    fun 주문할_상품_리스트가_보여진다() {
        // given
        every { view.showOrderProductList(any()) } just runs

        // when
        presenter.loadOrderProducts()

        // then
        verify(exactly = 1) { view.showOrderProductList(any()) }
    }

    @Test
    fun 사용자가_사용할_수_있는_포인트가_보여진다() {
        // given
        every { view.showAvailablePoint(any()) } just runs
        every {
            pointRepository.requestPoints(any(), any())
        } answers {
            val callback = args[0] as (Point) -> Unit
            callback(Point(2000))
        }

        // when
        presenter.loadAvailablePoints()

        // then
        verify(exactly = 1) { view.showAvailablePoint(any()) }
    }

    @Test
    fun 사용자가_지불해야_할_가격이_보여진다() {
        // given
        justRun { view.showTotalPayment(any()) }

        // when
        presenter.loadPayment()

        // then
        verify(exactly = 1) { view.showTotalPayment(any()) }
    }

    @Test
    fun 사용자가_실제로_지불해야_할_최종_가격이_보여진다() {
        // given
        justRun { view.showFinalPayment(any()) }

        // when
        presenter.calculateFinalPayment(1000)

        // then
        verify(exactly = 1) { view.showFinalPayment(any()) }
    }

    @Test
    fun 사용자가_주문하기를_눌러_주문을_하면_주문이_성공적으로_이뤄지고_메인_화면으로_이동한다() {
        // given
        justRun { view.navigateToHome(any()) }
        every {
            orderRepository.orderProduct(any(), any(), any())
        } answers {
            val callback = args[1] as () -> Unit
            callback()
        }

        // when
        presenter.order()

        // then
        verify(exactly = 1) { view.navigateToHome(any()) }
    }

    @Test
    fun 사용자가_뒤로가기를_누르면_메인_화면으로_돌아간다() {
        // given
        justRun { view.navigateToHome(any()) }

        // when
        presenter.navigateToHome(android.R.id.home)

        // then
        verify(exactly = 1) { view.navigateToHome(any()) }
    }
}
