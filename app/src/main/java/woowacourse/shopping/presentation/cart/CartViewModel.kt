package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toPresentation

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val uiState: LiveData<ResultState<Unit>> = _uiState
    private val _cartItems: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _isCheckAll: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCheckAll: LiveData<Boolean> = _isCheckAll
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        loadItems()
    }

    fun loadItems(currentPage: Int = 0) {
        _uiState.value = ResultState.Loading

        cartRepository.fetchPagedCartItems(currentPage) { result ->
            result
                .onSuccess { loadedItems ->
                    val oldItemsMap =
                        cartItems.value
                            .orEmpty()
                            .associateBy { cartItemUiModel -> cartItemUiModel.id }

                    val newItems =
                        loadedItems.map { newItem ->
                            oldItemsMap[newItem.cartId] ?: newItem.toPresentation()
                        }

                    _cartItems.postValue(newItems)
                    _uiState.postValue(ResultState.Success(Unit))
                }.onFailure {
                    _toastMessage.postValue(R.string.cart_toast_load_fail)
                }
        }
    }

    fun fetchSelectedInfo() {
        val checkedItem = cartItems.value?.filter { it.isSelected } ?: return
        allCheckOrUnchecked()
        _selectedTotalCount.postValue(checkedItem.sumOf { it.quantity })
        _selectedTotalPrice.postValue(checkedItem.sumOf { it.totalPrice })
    }

    fun deleteProduct(cartItem: CartItemUiModel) {
        cartRepository.deleteProduct(cartItem.product.id) { result ->
            result
                .onSuccess {
                    _toastMessage.value = R.string.cart_toast_delete_success
                    loadItems()
                }.onFailure {
                    _toastMessage.value = R.string.cart_toast_delete_fail
                }
        }
    }

    fun increaseQuantity(productId: Long) {
        cartRepository.increaseQuantity(productId) { result ->
            result
                .onSuccess {
                    updateQuantity(productId, 1)
                }.onFailure {
                    _toastMessage.value = R.string.cart_toast_increase_fail
                }
        }
    }

    fun decreaseQuantity(productId: Long) {
        val currentItems = cartItems.value ?: emptyList()
        val item = currentItems.find { it.product.id == productId } ?: return

        if (item.quantity == 1) {
            _toastMessage.value = R.string.cart_toast_invalid_quantity
            return
        }

        cartRepository.decreaseQuantity(productId) { result ->
            result
                .onSuccess {
                    updateQuantity(productId, -1)
                }.onFailure {
                    _toastMessage.value = R.string.cart_toast_decrease_fail
                }
        }
    }

    fun toggleItemChecked(cartId: Long) {
        val newCartItems =
            _cartItems.value?.map { if (it.id == cartId) it.copy(isSelected = !it.isSelected) else it }
                ?: return
        _cartItems.postValue(newCartItems)

        fetchSelectedInfo()
    }

    private fun allCheckOrUnchecked() {
        val isAllSelected = _cartItems.value?.all { it.isSelected } ?: false
        if (isAllSelected) _isCheckAll.value = true
        val isAllNotSelected = _cartItems.value?.all { !it.isSelected } ?: false
        if (isAllNotSelected) _isCheckAll.value = false
    }

    private fun updateQuantity(
        productId: Long,
        amount: Int,
    ) {
        val currentItems = cartItems.value ?: emptyList()
        val updatedItem =
            currentItems.map { cartItem ->
                if (cartItem.product.id == productId) {
                    cartItem.copy(
                        quantity = cartItem.quantity + amount,
                        totalPrice = (cartItem.quantity + amount) * (cartItem.totalPrice / cartItem.quantity),
                    )
                } else {
                    cartItem
                }
            }
        _cartItems.postValue(updatedItem)
        fetchSelectedInfo()
    }

    fun toggleItemCheckAll() {
        val currentCheckState = _isCheckAll.value ?: return
        val toggledState = !currentCheckState
        _isCheckAll.value = toggledState
        _cartItems.value = _cartItems.value?.map { it.copy(isSelected = toggledState) }.orEmpty()
    }
}
