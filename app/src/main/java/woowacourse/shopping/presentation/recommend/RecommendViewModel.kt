package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener
import woowacourse.shopping.presentation.toPresentation

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(),
    ItemClickListener,
    CartCounterClickListener {
    private lateinit var recentCategory: String
    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    fun fetchData(
        price: Int,
        count: Int,
    ) {
        _selectedTotalPrice.value = price
        _selectedTotalCount.value = count

        recentProductRepository.getMostRecentProduct { result ->
            result.onSuccess { recentProduct ->
                recentCategory = recentProduct?.category ?: ""

                productRepository.fetchPagingProducts(category = recentCategory) { result ->
                    result.onSuccess { products ->
                        val recommendProductsUiModel =
                            products
                                .asSequence()
                                .filter { it.quantity == 0 }
                                .map { it.toPresentation() }
                                .take(10)
                                .toList()
                        _recommendProducts.postValue(recommendProductsUiModel)
                    }
                }
            }
        }
    }

    override fun onClickProductItem(productId: Long) {
    }

    override fun onClickAddToCart(cartItem: CartItem) {
        cartRepository.insertProduct(cartItem.product, 1) { result ->
            result
                .onSuccess {
                    updateQuantity(productId = cartItem.product.productId, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_add_cart_fail
                }
        }
    }

    override fun onClickMinus(id: Long) {
        cartRepository.decreaseQuantity(id) { result ->
            result
                .onSuccess {
                    updateQuantity(id, -1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_decrease_fail
                }
        }
    }

    override fun onClickPlus(id: Long) {
        cartRepository.increaseQuantity(id) { result ->
            result
                .onSuccess {
                    updateQuantity(id, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    private fun updateQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentItems = _recommendProducts.value ?: return
        val updatedItem =
            currentItems.map {
                if (it.product.id == productId) it.copy(quantity = it.quantity + delta) else it
            }
        _recommendProducts.postValue(updatedItem)
    }
}
