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
    private val cartItemIds: List<Long>,
    private val orderRepository: OrderRepository,
) : ViewModel(), OnOrderListener {
    private val _recommendProducts = MutableLiveData<List<Product>>(emptyList())
    val recommendedProducts: LiveData<List<Product>> get() = _recommendProducts

    override fun createOrder() {
        thread {
            orderRepository.order(cartItemIds)
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
            cartItemIds: List<Long>,
            orderRepository: OrderRepository =
                OrderRemoteRepository(
                    ShoppingApp.orderSource,
                    ShoppingApp.productSource,
                    ShoppingApp.historySource,
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
