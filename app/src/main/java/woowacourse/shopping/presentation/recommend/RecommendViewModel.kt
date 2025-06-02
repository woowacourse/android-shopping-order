package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toPresentation

class RecommendViewModel(
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel() {
    private lateinit var recentCategory: String
    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        fetchData()
    }

    private fun fetchData() {
        recentProductRepository.getMostRecentProduct { result ->
            result
                .onSuccess { recentProduct ->
                    recentCategory = recentProduct?.category ?: ""

                    recommendProductsUseCase(recentCategory) { result ->
                        result
                            .onSuccess { products ->
                                val recommendItems = products.map { it.toPresentation() }
                                _recommendProducts.postValue(recommendItems)
                            }.onFailure { _toastMessage.postValue(R.string.recommend_toast_load_fail) }
                    }
                }.onFailure {
                    _toastMessage.postValue(R.string.recommend_toast_recent_load_fail)
                }
        }
    }

    fun fetchSelectedInfo(
        price: Int,
        count: Int,
    ) {
        _selectedTotalPrice.value = price
        _selectedTotalCount.value = count
    }

    fun addToCart(cartItem: CartItem) {
        cartRepository.insertProduct(cartItem.product, 1) { result ->
            result
                .onSuccess {
                    updateQuantity(productId = cartItem.product.productId, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_add_cart_fail
                }
        }
    }

    fun increaseQuantity(id: Long) {
        cartRepository.increaseQuantity(id) { result ->
            result
                .onSuccess {
                    updateQuantity(id, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    fun decreaseQuantity(id: Long) {
        cartRepository.decreaseQuantity(id) { result ->
            result
                .onSuccess {
                    updateQuantity(id, -1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_decrease_fail
                }
        }
    }

    private fun updateQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentItems = _recommendProducts.value ?: return
        val oldPrice = _selectedTotalPrice.value ?: 0
        val oldCount = _selectedTotalCount.value ?: 0
        val updatedItem =
            currentItems.map {
                if (it.product.id == productId) {
                    fetchSelectedInfo(oldPrice + (it.product.price * delta), oldCount + delta)
                    it.copy(isSelected = true, quantity = it.quantity + delta)
                } else {
                    it
                }
            }
        _recommendProducts.postValue(updatedItem)
    }
}
