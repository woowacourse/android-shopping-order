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
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel() {
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
        setupPriceCount()
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            recentProductRepository
                .getMostRecentProduct()
                .onSuccess { recentProduct ->
                    val recentCategory = recentProduct?.category ?: ""

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
                    val cartId =
                        cartRepository.getCartItemById(cartItem.product.id).getOrThrow()?.cartId
                            ?: throw Exception()
                    updateQuantity(
                        cartId = cartId,
                        productId = cartItem.product.id,
                        delta = 1,
                    )
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
                    updateQuantity(productId = id, delta = 1)
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
                    updateQuantity(productId = id, delta = -1)
                }.onFailure {
                    _toastMessage.value = R.string.product_toast_decrease_fail
                }
        }
    }

    private fun updateQuantity(
        cartId: Long = 0,
        productId: Long,
        delta: Int,
    ) {
        val updatedItems = updateRecommendProducts(cartId, productId, delta)
        val changedItem = updatedItems.first { it.product.id == productId }
        updateSelectedItem(changedItem)
        setupPriceCount()
    }

    private fun updateSelectedItem(item: CartItemUiModel) {
        if (!item.isSelected) return

        val currentSelected = _selectedItems.value.orEmpty().toMutableList()
        val index = currentSelected.indexOfFirst { it.product.id == item.product.id }

        when {
            item.quantity == 0 && index != -1 -> currentSelected.removeAt(index)
            item.quantity > 0 && index != -1 -> currentSelected[index] = item
            item.quantity > 0 -> currentSelected.add(item)
        }

        _selectedItems.value = currentSelected
    }

    private fun setupPriceCount() {
        val selectedItems = this.selectedItems.value ?: return
        _selectedTotalPrice.value = selectedItems.sumOf { it.totalPrice }
        _selectedTotalCount.value = selectedItems.sumOf { it.quantity }
    }

    private fun updateRecommendProducts(
        cartId: Long,
        productId: Long,
        delta: Int,
    ): List<CartItemUiModel> {
        val updated =
            _recommendProducts.value.orEmpty().map { item ->
                if (item.product.id == productId) {
                    val newQuantity = item.quantity + delta
                    item.copy(
                        id = cartId,
                        isSelected = true,
                        quantity = newQuantity,
                        totalPrice = item.product.price * newQuantity,
                    )
                } else {
                    item
                }
            }
        _recommendProducts.value = updated
        return updated
    }
}
