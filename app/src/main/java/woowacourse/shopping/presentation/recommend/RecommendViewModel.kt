package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase
import woowacourse.shopping.presentation.Extra.KEY_SELECT_ITEMS
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toPresentation

class RecommendViewModel(
    savedStateHandle: SavedStateHandle,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel() {
    private lateinit var recentCategory: String

    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _selectedItems: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val selectedItems: LiveData<List<CartItemUiModel>> = _selectedItems
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData()
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData()
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        val initialItems =
            savedStateHandle.get<ArrayList<CartItemUiModel>>(KEY_SELECT_ITEMS) ?: emptyList()
        _selectedItems.value = initialItems
        setupPriceCount(initialItems)
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            recentProductRepository
                .getMostRecentProduct()
                .onSuccess { recentProduct ->
                    recentCategory = recentProduct?.category ?: ""

                    recommendProductsUseCase(recentCategory)
                        .onSuccess { products ->
                            val recommendItems = products.map { it.toPresentation() }
                            _recommendProducts.value = recommendItems
                        }.onFailure {
                            _toastMessage.value = R.string.recommend_toast_load_fail
                        }
                }.onFailure {
                    _toastMessage.value = R.string.recommend_toast_recent_load_fail
                }
        }
    }

    fun addToCart(cartItem: CartItemUiModel) {
        viewModelScope.launch {
            cartRepository
                .insertProduct(cartItem.product.toDomain(), 1)
                .onSuccess {
                    updateQuantity(productId = cartItem.product.id, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_add_cart_fail
                }
        }
    }

    fun increaseQuantity(id: Long) {
        viewModelScope.launch {
            cartRepository
                .increaseQuantity(id)
                .onSuccess {
                    updateQuantity(id, 1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_increase_fail
                }
        }
    }

    fun decreaseQuantity(id: Long) {
        viewModelScope.launch {
            cartRepository
                .decreaseQuantity(id)
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
        _recommendProducts.value = updatedItem
    }

    private fun setupPriceCount(items: List<CartItemUiModel>) {
        _selectedTotalPrice.value = items.sumOf { it.totalPrice }
        _selectedTotalCount.value = items.sumOf { it.quantity }
    }

    private fun updateSelectedInfo(
        priceDelta: Int,
        countDelta: Int,
    ) {
        val oldPrice = _selectedTotalPrice.value ?: return
        val oldCount = _selectedTotalCount.value ?: return
        _selectedTotalPrice.value = oldPrice + priceDelta
        _selectedTotalCount.value = oldCount + countDelta
    }
}
