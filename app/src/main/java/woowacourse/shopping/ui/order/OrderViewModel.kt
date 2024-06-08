package woowacourse.shopping.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.common.ResponseHandlingUtils.onError
import woowacourse.shopping.data.common.ResponseHandlingUtils.onException
import woowacourse.shopping.data.common.ResponseHandlingUtils.onSuccess
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.order.listener.OnOrderListener

class OrderViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel(), OnOrderListener, OnItemQuantityChangeListener {
    private val _recommendProducts = MutableLiveData<List<Product>>(emptyList())
    val recommendProducts: LiveData<List<Product>> get() = _recommendProducts

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _ordersCount = MutableLiveData(orderInformation.ordersCount)
    val ordersCount: LiveData<Int> get() = _ordersCount

    private val _isOrderSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData(false)
    val isOrderSuccess: SingleLiveData<Boolean> get() = _isOrderSuccess

    override fun createOrder() {
        viewModelScope.launch {
            val recommendProducts: List<Product> = recommendProducts.value ?: return@launch
            val addedProductIds: List<Long> = recommendProducts.filter { it.quantity != 0 }.map { it.id }
            val cartItems: List<CartItem> = cartItemRepository.loadCartItems()
            val cartItemIds: List<Long> = cartItems.filter { it.product.id in addedProductIds }.map { it.id }
            orderRepository.orderCartItems(orderInformation.cartItemIds + cartItemIds)
            _isOrderSuccess.setValue(true)
        }
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            try {
                cartItemRepository.updateProductQuantity(productId, quantity)
            } catch (e: NoSuchElementException) {
                cartItemRepository.updateProductQuantity(productId, quantity)
            } finally {
                updateProductQuantity(productId, INCREASE_VARIATION)
                productRepository.loadProduct(productId).onSuccess { product ->
                    updateOrderAmount(product.price)
                }.onError { code, message ->
                    // TODO: Error Handling
                }.onException {
                    // TODO: Exception Handling
                }
                updateOrdersCount(INCREASE_VARIATION)
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productId, quantity)
            updateProductQuantity(productId, DECREASE_VARIATION)
            productRepository.loadProduct(productId).onSuccess { product ->
                updateOrderAmount(-product.price)
            }.onError { code, message ->
                // TODO: Error Handling
            }.onException {
                // TODO: Exception Handling
            }
            updateOrdersCount(DECREASE_VARIATION)
        }
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            _recommendProducts.postValue(orderRepository.loadRecommendedProducts())
        }
    }

    private fun updateProductQuantity(
        productId: Long,
        variation: Int,
    ) {
        _recommendProducts.value =
            recommendProducts.value?.map { product ->
                val quantity: Int = product.quantity + variation
                product.takeIf { it.id == productId }?.copy(quantity = quantity) ?: product
            }
    }

    private fun updateOrderAmount(amountVariation: Int) {
        val currentOrderAmount = orderAmount.value ?: 0
        _orderAmount.value = currentOrderAmount + amountVariation
    }

    private fun updateOrdersCount(countVariation: Int) {
        val currentOrdersCount = ordersCount.value ?: 0
        _ordersCount.value = currentOrdersCount + countVariation
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
