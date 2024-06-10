package woowacourse.shopping.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CategoryBasedProductRecommendationRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository2
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository2
import woowacourse.shopping.domain.repository.ProductsRecommendationRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.ui.order.event.OrderEvent
import woowacourse.shopping.ui.order.listener.OrderListener
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class OrderViewModel(
    private val orderRepository2: OrderRepository2,
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository2.loadAllOrders().getOrThrow().also {
                Log.d(TAG, "all orders: $it")
            }
        }

    }

    override fun order() {
        viewModelScope.launch(Dispatchers.IO) {
            _event.postValue(OrderEvent.CompleteOrder)

        }
    }

    fun loadAll() {
        viewModelScope.launch(Dispatchers.IO) {
            productsRecommendationRepository.recommendedProducts()
                .onSuccess { recommendedProducts ->
                    withContext(Dispatchers.Main) {
                        _recommendedProducts.postValue(recommendedProducts)
                    }
                }
                .onFailure {
                    // TODO : handle error
                    Log.e(TAG, "loadAll: recommendedProducts2 $it")
                    throw it
                }

            orderRepository2.allOrderItemsQuantity()
                .onSuccess { allQuantity ->
                    withContext(Dispatchers.Main) {
                        _addedProductQuantity.postValue(allQuantity)
                    }
                }
                .onFailure {
                    // TODO : handle error
                    Log.e(TAG, "loadAll: allOrderItemsQuantity2 $it")
                    throw it
                }

            orderRepository2.orderItemsTotalPrice()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _totalPrice.postValue(it)
                    }
                }
                .onFailure {
                    // TODO : handle error
                    Log.e(TAG, "loadAll: orderItemsTotalPrice2 $it")
                    throw it
                }


        }

    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.addShoppingCartProduct(productId, INCREASE_AMOUNT)
                .onSuccess {
                    Log.d(TAG, "onIncrease: save in cart success")
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "onIncrease: addShoppingCartProduct2: $it")
                    throw it
                }

            orderRepository2.updateOrderItem(productId, quantity)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        updateRecommendProductsQuantity(productId, INCREASE_AMOUNT)
                        updateTotalQuantity(productId, INCREASE_AMOUNT)
                        _addedProductQuantity.postValue(_addedProductQuantity.value?.plus(1) ?: 1)
                    }
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "onIncrease: saveOrderItem2: $it")
                    throw it
                }


            Log.d(TAG, "onDecrease: orderREpo2: ${orderRepository2.loadAllOrders().getOrThrow()}")


        }


    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository2.updateOrderItem(productId, quantity)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        updateRecommendProductsQuantity(productId, DECREASE_AMOUNT)
                        updateTotalQuantity(productId, DECREASE_AMOUNT)
                        _addedProductQuantity.postValue(_addedProductQuantity.value?.minus(1) ?: 0)
                    }
                }
                .onFailure {
                    // TODO: handle error
                    Log.e(TAG, "onDecrease: saveOrderItem2: $it")
                    throw it
                }
            Log.d(TAG, "onDecrease: orderREpo2: ${orderRepository2.loadAllOrders().getOrThrow()}")

        }
    }

    private fun updateTotalQuantity(
        productId: Long,
        changeAmount: Int,
    ) {
        _totalPrice.postValue(_totalPrice.value?.plus(productQuantity(productId) * changeAmount) ?: 0)
    }

    private fun productQuantity(productId: Long) =
        _recommendedProducts.getValue()?.find { it.id == productId }?.price ?: 0

    private fun updateRecommendProductsQuantity(
        productId: Long,
        changeAmount: Int,
    ) {
        _recommendedProducts.postValue(
            _recommendedProducts.getValue()?.map { product ->
                if (product.id == productId) {
                    product.copy(quantity = product.quantity + changeAmount)
                } else {
                    product
                }
            } ?: emptyList(),
        )
    }

    companion object {
        private const val TAG = "OrderViewModel"

        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1

        fun factory(
            orderRepository2: OrderRepository2 =
                DefaultOrderRepository2(
                    ShoppingApp.orderSource2,
                    ShoppingApp.cartSource,

                    ),
            productRecommendationRepository: ProductsRecommendationRepository =
                CategoryBasedProductRecommendationRepository(
                    ShoppingApp.productSource,
                    ShoppingApp.cartSource,
                    ShoppingApp.historySource
                ),
            cartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    orderRepository2,
                    productRecommendationRepository,
                    cartRepository,
                )
            }
        }
    }
}
