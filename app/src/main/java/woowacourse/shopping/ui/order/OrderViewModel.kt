package woowacourse.shopping.ui.order

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.order.listener.OnOrderListener
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

    private val _isOrderSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)
    val isOrderSuccess: SingleLiveData<Boolean> get() = _isOrderSuccess

    override fun createOrder() {
        thread {
            val recommendProducts: List<Product> = recommendProducts.value ?: return@thread
            val addedProductIds: List<Long> = recommendProducts.filter { it.quantity != 0 }.map { it.id }
            val cartItems: List<CartItem> = cartItemRepository.loadCartItems()
            val cartItemIds: List<Long> = cartItems.filter { it.product.id in addedProductIds }.map { it.id }
            orderRepository.order(orderInformation.cartItemIds + cartItemIds)
            uiHandler.post {
                _isOrderSuccess.setValue(true)
            }
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
            _recommendProducts.postValue(orderRepository.recommendedProducts())
        }
    }

    private fun updateProductQuantity(
        productId: Long,
        variation: Int,
    ) {
        uiHandler.post {
            _recommendProducts.value =
                recommendProducts.value?.map { product ->
                    val quantity: Int = product.quantity + variation
                    product.takeIf { it.id == productId }?.copy(quantity = quantity) ?: product
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
            orderRepository: OrderRepository =
                OrderRemoteRepository(
                    ShoppingApp.orderSource,
                    ShoppingApp.productSource,
                    ShoppingApp.historySource,
                    ShoppingApp.cartSource,
                ),
            cartItemRepository: CartItemRepository =
                DefaultCartItemRepository(
                    ShoppingApp.cartSource,
                ),
            productRepository: ProductRepository =
                DefaultProductRepository(
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
