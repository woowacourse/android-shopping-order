package woowacourse.shopping

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrderSubmitDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.view.orderhistory.OrderHistoryContract
import woowacourse.shopping.view.orderhistory.OrderHistoryPresenter

class OrderHistoryPresenterTest {
    private lateinit var view: OrderHistoryContract.View
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = object : OrderRepository {
            override fun getAll(callback: (OrdersDTO) -> Unit) {
                callback(
                    OrdersDTO(
                        listOf(
                            OrderSubmitDTO(
                                1,
                                "2023-02-03 11:11:00",
                                listOf(ProductWithQuantity(Product(1, "현미밥", Price(10000), ""), 1)),
                                10000,
                            ),
                        ),
                    ),
                )
            }

            override fun getOrder(id: Int, callback: (OrderSubmitDTO) -> Unit) {
                // val orderId: Int, val orderedDateTime: String, val products: List<ProductWithQuantity>, val totalPrice: Int
            }

            override fun order(cartProducts: OrderCartItemsDTO, callback: (Int?) -> Unit) {
            }
        }
        presenter = OrderHistoryPresenter(
            view,
            orderRepository,
        )
    }

    @Test
    fun 주문_정보를_띄울_수_있다() {
        // given

        // when
        presenter.fetchOrders()

        // then
        verify(exactly = 1) { view.showOrders(any()) }
    }
}
