package woowacourse.shopping.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository
import kotlin.concurrent.thread

class OrderViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
) : ViewModel(), OnOrderListener {
    private val _recommendProducts = MutableLiveData<List<Product>>(emptyList())
    val recommendedProducts: LiveData<List<Product>> get() = _recommendProducts

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _ordersCount = MutableLiveData(orderInformation.ordersCount)
    val ordersCount: LiveData<Int> get() = _ordersCount

    override fun createOrder() {
        thread {
            orderRepository.order(orderInformation.cartItemIds)
        }
    }

    fun loadRecommendedProducts() {
        thread {
            _recommendProducts.postValue(orderRepository.recommendedProducts().map { it.toDomain() })
        }
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        fun factory(
            orderInformation: OrderInformation,
            orderRepository: OrderRepository =
                OrderRemoteRepository(
                    ShoppingApp.orderSource,
                    ShoppingApp.productSource,
                    ShoppingApp.historySource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    orderInformation,
                    orderRepository,
                )
            }
        }
    }
}
