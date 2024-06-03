package woowacourse.shopping.ui.order

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import kotlin.concurrent.thread

class OrderViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel(), OnOrderListener, OnItemQuantityChangeListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    private val _recommendProducts = MutableLiveData<List<Product>>(emptyList())
    val recommendProducts: LiveData<List<Product>> get() = _recommendProducts

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _ordersCount = MutableLiveData(orderInformation.ordersCount)
    val ordersCount: LiveData<Int> get() = _ordersCount

    override fun createOrder() {
        thread {
            orderRepository.order(orderInformation.cartItemIds)
        }
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            try {
                cartItemRepository.increaseCartProduct(productId, quantity)
            } catch (e: NoSuchElementException) {
                cartItemRepository.addCartItem(productId, quantity)
            } finally {
                updateProductQuantity(productId, INCREASE_VARIATION)
                val product = productRepository.loadProduct(productId)
                updateOrderAmount(product.price)
                updateOrdersCount(INCREASE_VARIATION)
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            cartItemRepository.decreaseCartProduct(productId, quantity)
            updateProductQuantity(productId, DECREASE_VARIATION)
            val product = productRepository.loadProduct(productId)
            updateOrderAmount(-product.price)
            updateOrdersCount(DECREASE_VARIATION)
        }
    }

    fun loadRecommendedProducts() {
        thread {
            _recommendProducts.postValue(
                orderRepository.recommendedProducts().map { it.toDomain() })
        }
    }

    private fun updateProductQuantity(
        productId: Long,
        variation: Int,
    ) {
        uiHandler.post {
            _recommendProducts.value = recommendProducts.value?.map { product ->
                product.takeIf { it.id == productId }?.copy(quantity = product.quantity + variation)
                    ?: product
            }
        }
    }

    private fun updateOrderAmount(amountVariation: Int) {
        val currentOrderAmount = orderAmount.value ?: 0
        uiHandler.post {
            _orderAmount.value = currentOrderAmount + amountVariation
        }
    }

    private fun updateOrdersCount(countVariation: Int) {
        val currentOrdersCount = ordersCount.value ?: 0
        uiHandler.post {
            _ordersCount.value = currentOrdersCount + countVariation
        }
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"
        private const val INCREASE_VARIATION = 1
        private const val DECREASE_VARIATION = -1

        fun factory(
            orderInformation: OrderInformation,
            orderRepository: OrderRepository = OrderRemoteRepository(
                ShoppingApp.orderSource,
                ShoppingApp.productSource,
                ShoppingApp.historySource,
            ),
            cartItemRepository: CartItemRepository = DefaultCartItemRepository(
                ShoppingApp.cartSource,
            ),
            productRepository: ProductRepository = DefaultProductRepository(
                ShoppingApp.productSource,
                ShoppingApp.cartSource,
            ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    orderInformation,
                    orderRepository,
                    cartItemRepository,
                    productRepository,
                )
            }
        }
    }
}
