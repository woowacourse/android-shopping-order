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
import woowacourse.shopping.data.cart.remote.DefaultCartItemRepository
import woowacourse.shopping.data.order.remote.OrderRemoteRepository
import woowacourse.shopping.data.product.remote.DefaultProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.model.ProductIdsCount.Companion.DECREASE_VARIATION
import woowacourse.shopping.domain.model.ProductIdsCount.Companion.INCREASE_VARIATION
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.order.listener.OnNavigationPaymentListener

class OrderViewModel(
    private val orderInformation: OrderInformation,
    private val orderRepository: OrderRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel(), OnNavigationPaymentListener, OnItemQuantityChangeListener {
    private val _recommendProducts = MutableLiveData<List<Product>>(emptyList())
    val recommendProducts: LiveData<List<Product>> get() = _recommendProducts

    private val _orderAmount = MutableLiveData(orderInformation.orderAmount)
    val orderAmount: LiveData<Int> get() = _orderAmount

    private val _ordersCount = MutableLiveData(orderInformation.ordersCount)
    val ordersCount: LiveData<Int> get() = _ordersCount

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var _navigationPaymentEvent = MutableSingleLiveData<OrderInformation>()
    val navigationPaymentEvent: SingleLiveData<OrderInformation> get() = _navigationPaymentEvent

    override fun onOrderClick() {
        viewModelScope.launch {
            val recommendProducts: List<Product> = recommendProducts.value ?: return@launch
            val addedProductIds: List<Long> = recommendProducts.filter { it.quantity != 0 }.map { it.id }
            handleResponseResult(cartItemRepository.loadCartItems(), _errorMessage) { cartItems ->
                val cartItemIds = cartItems.filter { it.product.id in addedProductIds }.map { it.id }
                _navigationPaymentEvent.setValue(
                    OrderInformation(
                        cartItemIds = orderInformation.cartItemIds + cartItemIds,
                        orderAmount = orderAmount.value ?: 0,
                        ordersCount = ordersCount.value ?: 0,
                    )
                )
            }
        }
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        updateQuantity(ProductIdsCount(productId, quantity), INCREASE_VARIATION) { price -> price }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        updateQuantity(ProductIdsCount(productId, quantity), DECREASE_VARIATION) { price -> -price }
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            handleResponseResult(orderRepository.loadRecommendedProducts(), _errorMessage) { products ->
                _recommendProducts.value = products
            }
        }
    }

    private fun updateQuantity(
        productQuantity: ProductIdsCount,
        variation: Int,
        priceConvert: (price: Int) -> Int
    ) {
        viewModelScope.launch {
            cartItemRepository.updateProductQuantity(productQuantity.productId, productQuantity.quantity)
            updateProductQuantity(productQuantity.productId, variation)
            updateOrderAmount(productQuantity.productId, priceConvert)
            updateOrdersCount(variation)
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

    private suspend fun updateOrderAmount(productId: Long, priceConvert: (price: Int) -> Int) {
        handleResponseResult(productRepository.loadProduct(productId), _errorMessage) { product ->
            val currentOrderAmount = orderAmount.value ?: 0
            _orderAmount.value = currentOrderAmount + priceConvert(product.price)
        }
    }

    private fun updateOrdersCount(countVariation: Int) {
        val currentOrdersCount = ordersCount.value ?: 0
        _ordersCount.value = currentOrdersCount + countVariation
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
