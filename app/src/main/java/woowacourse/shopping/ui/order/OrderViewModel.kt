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
    private val orderItemsId: List<Long>,
    private val orderRepository: OrderRepository,
    private val historyRepository: ProductHistoryRepository,
    private val productsRecommendationRepository: ProductsRecommendationRepository,
    private val cartRepository: ShoppingCartRepository,
) : ViewModel(), OnOrderListener, OnItemQuantityChangeListener {
    private val _recommendedProducts: MutableSingleLiveData<List<Product>> = MutableSingleLiveData()
    val recommendedProducts: SingleLiveData<List<Product>> get() = _recommendedProducts

    private val _addedProductQuantity: MutableLiveData<Int> = MutableLiveData()
    val addedProductQuantity: LiveData<Int> get() = _addedProductQuantity

    private val _changedProduct: MutableSingleLiveData<Product> = MutableSingleLiveData()
    val changedProduct: SingleLiveData<Product> get() = _changedProduct

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

        _addedProductQuantity.postValue(orderRepository.allOrderItemsTempQuantity())

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
                ),
            cartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory {
            return UniversalViewModelFactory {
                OrderViewModel(
                    productIds, orderRepository, historyRepository, productRecommendationRepository, cartRepository
                )
            }
        }

    }

    override fun onIncrease(productId: Long, quantity: Int) {
        thread {
            try {
                cartRepository.updateProductQuantity(productId, quantity)
                Log.d(TAG, "onIncrease: updateProductQuantity")
            } catch (e: NoSuchElementException) {
                Log.d(TAG, "catch NoSuchElementException $e")
                cartRepository.addShoppingCartProduct(productId, quantity)
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

            }
            _addedProductQuantity.postValue(_addedProductQuantity.value?.plus(1) ?: 1)

        }.join()
    }

    override fun onDecrease(productId: Long, quantity: Int) {
        TODO("Not yet implemented")
    }
}
