package woowacourse.shopping.ui.order

import android.util.Log
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
import woowacourse.shopping.ui.OnItemQuantityChangeListener
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

    override fun createOrder() {
        thread {
            orderRepository.order(
                orderRepository.loadOrderItemTemp().map {
                    it.key
                }
            )
        }.join()
    }

    fun loadAll() {
        thread {
            _recommendedProducts.postValue(
                productsRecommendationRepository.recommendedProducts(
                    productId = historyRepository.loadLatestProduct().id
                )
            )
            _addedProductQuantity.postValue(orderRepository.allOrderItemsTempQuantity())
            _totalPrice.postValue(orderRepository.tempOrderItemsTotalPrice())
        }.join()
    }


    override fun onIncrease(productId: Long, quantity: Int) {
        thread {
            try {
                cartRepository.updateProductQuantity(productId, quantity)
                orderRepository.saveOrderItemTemp(productId, quantity).also {
                    Log.d(TAG, "onIncrease: orderRepository: ${orderRepository.loadOrderItemTemp()}")
                }
                Log.d(TAG, "onIncrease: updateProductQuantity")
            } catch (e: NoSuchElementException) {
                Log.d(TAG, "catch NoSuchElementException $e")
                cartRepository.addShoppingCartProduct(productId, quantity)
                orderRepository.saveOrderItemTemp(productId, quantity).also {
                    Log.d(TAG, "onIncrease: orderRepository: ${orderRepository.loadOrderItemTemp()}")
                }
                Log.d(TAG, "onIncrease: addShoppingCartProduct")
            } catch (e: Exception) {
                Log.d(TAG, "catch Exception $e")
            } finally {
                _recommendedProducts.postValue(
                    _recommendedProducts.getValue()?.map {
                        if (it.id == productId) {
                            it.copy(quantity = it.quantity + 1)
                        } else {
                            it
                        }
                    } ?: emptyList()
                )

                _totalPrice.postValue(
                    _totalPrice.value?.plus(
                        _recommendedProducts.getValue()?.find { it.id == productId }?.price ?: 0
                    ) ?: 0
                )

            }
            _addedProductQuantity.postValue(_addedProductQuantity.value?.plus(1) ?: 1)

        }.join()
    }

    override fun onDecrease(productId: Long, quantity: Int) {
        thread {
            val item = _recommendedProducts.getValue()?.find {
                it.id == productId
            } ?: throw NoSuchElementException("There is no product with id: $productId")

            cartRepository.updateProductQuantity(productId, item.quantity - 1)

            _recommendedProducts.postValue(
                _recommendedProducts.getValue()?.map {
                    if (it.id == productId) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                } ?: emptyList()
            )

            _totalPrice.postValue(
                _totalPrice.value?.minus(
                    _recommendedProducts.getValue()?.find { it.id == productId }?.price ?: 0
                ) ?: 0
            )

            _addedProductQuantity.postValue(_addedProductQuantity.value?.minus(1) ?: 0)
        }.join()

    }

    companion object {
        private const val TAG = "OrderViewModel"

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
