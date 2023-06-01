package woowacourse.shopping.ui.order

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository

internal class OrderPresenterTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var view: OrderContract.View

    @Before
    fun setUp() {
        orderRepository = mockk()
        view = mockk()
    }

    @Test
    internal fun 장바구니에_담은_상품을_주문할_수_있다() {
        // Given: 장바구니에서 주문할 상품을 선택한다.
        val order: Order = mockk(relaxed = true)
        justRun { view.showOrderCompleted() }
        justRun { orderRepository.saveOrder(order, {}, {}) }
        presenter = OrderPresenter(view, order, orderRepository)

        // When: 상품을 주문한다.
        presenter.order()

        // Then: 주문이 완료되면 성공 메시지를 보여준다.
        verify { orderRepository.saveOrder(order, {}, {}) }
        verify { view.showOrderCompleted() }
    }

    // val cartProduct = CartProduct(
    //            id = 1,
    //            product = Product(id = 1, name = "", price = Price(3000), imageUrl = "image_url"),
    //            selectedCount = ProductCount(5),
    //            isChecked = true,
    //        )
}
