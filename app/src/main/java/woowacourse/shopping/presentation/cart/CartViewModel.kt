package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.toPresentation

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems
    private val _isCheckAll: MutableLiveData<Boolean> = MutableLiveData()
    val isCheckAll: LiveData<Boolean> = _isCheckAll
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        loadItems()
    }

    fun loadItems(currentPage: Int = 0) {
        cartRepository.fetchPagedCartItems(currentPage) { result ->
            result
                .onSuccess { cartItems -> _cartItems.postValue(cartItems.map { it.toPresentation() }) }
                .onFailure { _toastMessage.postValue(R.string.cart_toast_load_fail) }
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
}
