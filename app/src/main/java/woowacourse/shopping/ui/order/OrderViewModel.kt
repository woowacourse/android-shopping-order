package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import kotlin.concurrent.thread

class OrderViewModel(
    private val cartItemIds: List<Long>,
    private val orderRepository: OrderRepository,
) : ViewModel(), OnOrderListener {
    override fun createOrder() {
        thread {
            orderRepository.order(cartItemIds)
        }
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        fun factory(
            cartItemIds: List<Long>,
            orderRepository: OrderRepository =
                OrderRemoteRepository(
                    ShoppingApp.orderSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    cartItemIds,
                    orderRepository,
                )
            }
        }
    }
}
