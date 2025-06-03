package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.toPresentation

class CartViewModel(
    private val cartRepository: CartRepository,
    private val increaseProductQuantityUseCase: IncreaseProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val uiState: LiveData<ResultState<Unit>> = _uiState
    private val _cartItems: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems
    private val _selectedTotalPrice: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cartItems) { cartItems ->
                value = cartItems?.filter { it.isSelected }?.sumOf { it.totalPrice } ?: 0
            }
        }
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cartItems) { cartItems ->
                value = cartItems?.filter { it.isSelected }?.sumOf { it.quantity } ?: 0
            }
        }
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _isCheckAll: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSource(_cartItems) { cartItems ->
                value = cartItems?.all { it.isSelected } ?: false
            }
        }
    val isCheckAll: LiveData<Boolean> = _isCheckAll
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

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
        increaseProductQuantityUseCase(
            productId,
            onSuccess = { updateQuantity(productId, 1) },
            onFailure = { _toastMessage.value = R.string.cart_toast_increase_fail },
        )
    }

    fun decreaseQuantity(productId: Long) {
        val currentItems = cartItems.value ?: emptyList()
        val item = currentItems.find { it.product.id == productId } ?: return

        if (item.quantity == 1) {
            _toastMessage.value = R.string.cart_toast_invalid_quantity
            return
        }

        decreaseProductQuantityUseCase(
            productId,
            onSuccess = { updateQuantity(productId, -1) },
            onFailure = { _toastMessage.value = R.string.cart_toast_decrease_fail },
        )
    }

    fun toggleItemChecked(cartId: Long) {
        val newCartItems =
            _cartItems.value?.map { if (it.id == cartId) it.copy(isSelected = !it.isSelected) else it }
                ?: return
        _cartItems.postValue(newCartItems)
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
    }

    fun toggleItemCheckAll() {
        val currentCheckState = _isCheckAll.value ?: return
        val toggledState = !currentCheckState
        _cartItems.value = _cartItems.value?.map { it.copy(isSelected = toggledState) }.orEmpty()
    }
}
