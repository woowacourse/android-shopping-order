package woowacourse.shopping.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CategoryBasedProductRecommendationRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.ui.order.event.OrderError
import woowacourse.shopping.ui.order.event.OrderEvent
import woowacourse.shopping.ui.order.listener.OrderListener
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val productsRecommendationRepository: ProductsRecommendationRepository,
) : ViewModel(), OrderListener {
    private val _recommendedProducts: MutableSingleLiveData<List<Product>> = MutableSingleLiveData()
    val recommendedProducts: SingleLiveData<List<Product>> get() = _recommendedProducts

    private val _addedProductQuantity: MutableLiveData<Int> = MutableLiveData()
    val addedProductQuantity: LiveData<Int> get() = _addedProductQuantity

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _event: MutableSingleLiveData<OrderEvent> = MutableSingleLiveData()
    val event: SingleLiveData<OrderEvent> get() = _event

    private val _error: MutableSingleLiveData<OrderError> = MutableSingleLiveData()
    val error: SingleLiveData<OrderError> get() = _error

    fun loadAll() {
        viewModelScope.launch {
            loadRecommendProducts()
            countOrderItemsQuantity()
            calculateOrderItemsTotalPrice()
        }
    }

    private suspend fun loadRecommendProducts() {
        productsRecommendationRepository.recommendedProducts()
            .onSuccess { products ->
                _recommendedProducts.setValue(products)
            }
            .onFailure {
                _error.setValue(OrderError.LoadRecommendedProducts)
            }
    }

    private suspend fun countOrderItemsQuantity() {
        orderRepository.allOrderItemsQuantity()
            .onSuccess { allOrderItemsQuantity ->
                _addedProductQuantity.value = allOrderItemsQuantity
            }
            .onFailure {
                _error.setValue(OrderError.CalculateOrderItemsQuantity)
            }
    }

    private suspend fun calculateOrderItemsTotalPrice() {
        orderRepository.orderItemsTotalPrice()
            .onSuccess { calculatedTotalPrice ->
                _totalPrice.value = calculatedTotalPrice
            }
            .onFailure {
                _error.setValue(OrderError.CalculateOrderItemsTotalPrice)
            }
    }

    override fun order() {
        _event.setValue(OrderEvent.CompleteOrder)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            updateItemQuantity(productId, quantity, INCREASE_AMOUNT)
        }
    }

    private suspend fun updateItemQuantity(
        productId: Long,
        quantity: Int,
        changeAmount: Int,
    ) {
        orderRepository.updateOrderItem(productId, quantity)
            .onSuccess {
                updateRecommendProductsQuantity(productId, changeAmount)
                updateTotalPrice(productId, changeAmount)
                _addedProductQuantity.value = addedProductQuantity.value?.plus(changeAmount)
            }
            .onFailure {
                _error.setValue(OrderError.UpdateOrderItem)
            }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            updateItemQuantity(productId, quantity, DECREASE_AMOUNT)
        }
    }

    private fun updateTotalPrice(
        productId: Long,
        changeAmount: Int,
    ) {
        _totalPrice.value = totalPrice.value?.plus(productPrice(productId) * changeAmount)
    }

    private fun productPrice(productId: Long) = _recommendedProducts.getValue()?.find { product -> product.id == productId }?.price ?: 0

    private fun updateRecommendProductsQuantity(
        productId: Long,
        changeAmount: Int,
    ) {
        _recommendedProducts.setValue(
            recommendedProducts.getValue().orEmpty().map { product ->
                if (product.id == productId) {
                    product.copy(quantity = product.quantity + changeAmount)
                } else {
                    product
                }
            },
        )
    }

    companion object {
        private const val TAG = "OrderViewModel"

        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1

        fun factory(
            orderRepository: OrderRepository =
                DefaultOrderRepository(
                    ShoppingApp.orderSource,
                    ShoppingApp.cartSource,
                ),
            productRecommendationRepository: ProductsRecommendationRepository =
                CategoryBasedProductRecommendationRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource,
                    ShoppingApp.historySource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    orderRepository,
                    productRecommendationRepository,
                )
            }
        }
    }
}
