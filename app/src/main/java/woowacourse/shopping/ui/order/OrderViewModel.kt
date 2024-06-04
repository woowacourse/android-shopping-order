package woowacourse.shopping.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import woowacourse.shopping.remote.source.OrderRemoteDataSource

class OrderViewModel(
    private val cartItemIds: List<Long>,
    private val orderRepository: OrderRepository,
) : ViewModel(), OnOrderListener {
    override fun createOrder() {
        Log.d(TAG, "createOrder: orderCLicked")
        orderRepository.order(cartItemIds)
        Log.d(TAG, cartItemIds.toString())
    }

    companion object {
        private const val TAG = "OrderViewModel"

        fun factory(
            productIds: List<Long>,
            orderRepository: OrderRepository = DefaultOrderRepository(ShoppingApp.orderSource)
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    productIds, orderRepository
                )
            }
        }

    }
}