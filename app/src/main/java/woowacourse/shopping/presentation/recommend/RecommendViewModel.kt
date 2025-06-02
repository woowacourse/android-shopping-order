package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase
import woowacourse.shopping.presentation.Extra.KEY_SELECT_COUNT
import woowacourse.shopping.presentation.Extra.KEY_SELECT_PRICE
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toPresentation

class RecommendViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel() {
    private lateinit var recentCategory: String
    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts

    val selectedTotalPrice: LiveData<Int> = savedStateHandle.getLiveData(KEY_SELECT_PRICE, 0)
    val selectedTotalCount: LiveData<Int> = savedStateHandle.getLiveData(KEY_SELECT_COUNT, 0)

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

    fun addToCart(cartItem: CartItemUiModel) {
        cartRepository.insertProduct(cartItem.product.toDomain(), 1) { result ->
            result
                .onSuccess {
                    updateQuantity(productId = cartItem.product.id, 1)
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
        val updatedItem =
            currentItems.map {
                if (it.product.id == productId) {
                    updateSelectedInfo(it.product.price * delta, delta)
                    it.copy(isSelected = true, quantity = it.quantity + delta)
                } else {
                    it
                }
            }
        _recommendProducts.postValue(updatedItem)
    }

    private fun updateSelectedInfo(
        priceDelta: Int,
        countDelta: Int,
    ) {
        val newPrice = (savedStateHandle.get<Int>(KEY_SELECT_PRICE) ?: 0) + priceDelta
        val newCount = (savedStateHandle.get<Int>(KEY_SELECT_COUNT) ?: 0) + countDelta
        savedStateHandle[KEY_SELECT_PRICE] = newPrice
        savedStateHandle[KEY_SELECT_COUNT] = newCount
    }
}
