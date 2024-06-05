package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CategoryBasedProductRecommendationRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import kotlin.concurrent.thread

class OrderViewModel(
    private val orderItemsId: List<Long>,
    private val orderRepository: OrderRepository,
    private val historyRepository: ProductHistoryRepository,
    private val productsRecommendationRepository: ProductsRecommendationRepository
) : ViewModel(), OnOrderListener {
    private val _recommendedProducts: MutableSingleLiveData<List<Product>> = MutableSingleLiveData()
    val recommendedProducts: SingleLiveData<List<Product>> get() = _recommendedProducts

    override fun createOrder() {
        orderRepository.order(orderItemsId)
    }

    fun loadAll() {
        thread {
            _recommendedProducts.postValue(
                productsRecommendationRepository.recommendedProducts(
                    productId = historyRepository.loadLatestProduct().id
                )
            )
        }.join()
    }


    companion object {
        private const val TAG = "OrderViewModel"

        fun factory(
            productIds: List<Long>,
            orderRepository: OrderRepository = DefaultOrderRepository(
                ShoppingApp.orderSource
            ),
            historyRepository: ProductHistoryRepository = DefaultProductHistoryRepository(
                ShoppingApp.historySource,
                ShoppingApp.productSource
            ),
            productRecommendationRepository: ProductsRecommendationRepository =
                CategoryBasedProductRecommendationRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource
                )
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    productIds, orderRepository, historyRepository, productRecommendationRepository
                )
            }
        }

    }
}
