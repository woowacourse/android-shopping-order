package woowacourse.shopping.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.remote.order.OrderRemoteDataSource

class OrderViewModel(
    private val cartItemIds: List<Long>,
    private val dataSource: OrderRemoteDataSource,
) : ViewModel(), OnOrderListener {
    override fun createOrder() {
        dataSource.order(cartItemIds)
        Log.d(TAG, cartItemIds.toString())
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        fun factory(
            cartItemIds: List<Long>,
            orderDataSource: OrderRemoteDataSource,
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    cartItemIds,
                    orderDataSource,
                )
            }
        }
    }
}
