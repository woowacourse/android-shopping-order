package woowacourse.shopping.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CategoryBasedProductRecommendationRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.DefaultProductHistoryRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.ui.order.event.OrderEvent
import woowacourse.shopping.ui.order.listener.OrderListener
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import kotlin.concurrent.thread

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val historyRepository: ProductHistoryRepository,
    private val productsRecommendationRepository: ProductsRecommendationRepository,
    private val cartRepository: ShoppingCartRepository,
) : ViewModel(), OrderListener {
    private val _recommendedProducts: MutableSingleLiveData<List<Product>> = MutableSingleLiveData()
    val recommendedProducts: SingleLiveData<List<Product>> get() = _recommendedProducts

    private val _addedProductQuantity: MutableLiveData<Int> = MutableLiveData()
    val addedProductQuantity: LiveData<Int> get() = _addedProductQuantity

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _event: MutableSingleLiveData<OrderEvent> = MutableSingleLiveData()
    val event: SingleLiveData<OrderEvent> get() = _event

    override fun order() {
        thread {
            val filter = orderRepository.orderItems().map { (id, _) -> id }

            orderRepository.order(filter)
            _event.postValue(OrderEvent.CompleteOrder)
        }
    }

    fun loadAll() {
        thread {
            _recommendedProducts.postValue(
                productsRecommendationRepository.recommendedProducts(
                    productId = historyRepository.loadLatestProduct().id
                )
            )
            _addedProductQuantity.postValue(orderRepository.allOrderItemsQuantity())

            _totalPrice.postValue(orderRepository.orderItemsTotalPrice())
        }
    }

    override fun onIncrease(productId: Long, quantity: Int) {
        thread {
            try {
                cartRepository.updateProductQuantity(productId, quantity)
                orderRepository.saveOrderItem(productId, quantity)
            } catch (e: NoSuchElementException) {
                cartRepository.addShoppingCartProduct(productId, quantity)
                orderRepository.saveOrderItem(productId, quantity)
            } finally {
                updateRecommendProductsQuantity(productId, INCREASE_AMOUNT)
                updateTotalQuantity(productId, INCREASE_AMOUNT)
            }
            _addedProductQuantity.postValue(_addedProductQuantity.value?.plus(1) ?: 1)
        }
    }

    override fun onDecrease(productId: Long, quantity: Int) {
        thread {
            val item = _recommendedProducts.getValue()?.find {
                it.id == productId
            } ?: throw NoSuchElementException("There is no product with id: $productId")

            cartRepository.updateProductQuantity(productId, item.quantity - 1)

            updateRecommendProductsQuantity(productId, DECREASE_AMOUNT)
            updateTotalQuantity(productId, DECREASE_AMOUNT)

            _addedProductQuantity.postValue(_addedProductQuantity.value?.minus(1) ?: 0)
        }
    }

    private fun updateTotalQuantity(productId: Long, changeAmount: Int) {
        _totalPrice.postValue(_totalPrice.value?.plus(productQuantity(productId) * changeAmount) ?: 0)
    }

    private fun productQuantity(productId: Long) =
        _recommendedProducts.getValue()?.find { it.id == productId }?.price ?: 0

    private fun updateRecommendProductsQuantity(productId: Long, changeAmount: Int) {
        _recommendedProducts.postValue(
            _recommendedProducts.getValue()?.map { product ->
                if (product.id == productId) {
                    product.copy(quantity = product.quantity + changeAmount)
                } else {
                    product
                }
            } ?: emptyList()
        )
    }

    companion object {
        private const val TAG = "OrderViewModel"

        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1

        fun factory(
            orderRepository: OrderRepository = DefaultOrderRepository(
                ShoppingApp.orderSource,
                ShoppingApp.productSource
            ),
            historyRepository: ProductHistoryRepository = DefaultProductHistoryRepository(
                ShoppingApp.historySource,
                ShoppingApp.productSource
            ),
            productRecommendationRepository: ProductsRecommendationRepository =
                CategoryBasedProductRecommendationRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource
                ),
            cartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    orderRepository, historyRepository, productRecommendationRepository, cartRepository
                )
            }
        }

    }
}
