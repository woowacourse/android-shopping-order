package woowacourse.shopping

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.orderdetail.OrderDetailContract
import woowacourse.shopping.view.orderdetail.OrderDetailPresenter

class OrderDetailPresenterTest {
    private lateinit var view: OrderDetailContract.View
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = object : OrderRepository {
            override fun getAll(callback: (OrdersDTO) -> Unit) {
            }

            override fun getOrder(id: Int, callback: (OrderDTO) -> Unit) {
                callback(
                    OrderDTO(
                        1,
                        "2023-02-03 11:11:00",
                        listOf(ProductWithQuantity(Product(1, "현미밥", Price(10000), ""), 1)),
                        10000,
                    ),
                )
            }

            override fun order(cartProducts: OrderCartItemsDTO, callback: (Int?) -> Unit) {
                callback(1)
            }
        }
        presenter = OrderDetailPresenter(
            1,
            view,
            orderRepository,
        )
    }

    @Test
    fun 주문_정보를_띄울_수_있다() {
        // given

        // when
        presenter.fetchOrder()

        // then
        verify(exactly = 1) { view.showOrderDetail(any()) }
    }
}
