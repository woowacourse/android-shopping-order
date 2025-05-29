package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.toPresentation

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems: MutableLiveData<ResultState<List<CartItemUiModel>>> = MutableLiveData()
    val cartItems: LiveData<ResultState<List<CartItemUiModel>>> = _cartItems
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        loadItems()
    }

    fun loadItems(currentPage: Int = 0) {
        cartRepository.fetchPagedCartItems(currentPage) { result ->
            result
                .onSuccess { cartItems -> _cartItems.postValue(ResultState.Success(cartItems.map { it.toPresentation() })) }
                .onFailure { _cartItems.postValue(ResultState.Failure()) }
        }
    }

    fun deleteProduct(cartItem: CartItem) {
        cartRepository.deleteProduct(cartItem.product.productId) { result ->
            result
                .onSuccess {
                    _toastMessage.value = R.string.cart_toast_delete_success
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
        val currentItems = (cartItems.value as? ResultState.Success)?.data ?: return
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
        val currentItems = (_cartItems.value as? ResultState.Success)?.data ?: return
        val updatedItem =
            currentItems.map {
                if (it.product.id == productId) it.copy(quantity = it.quantity + amount) else it
            }
        _cartItems.postValue(ResultState.Success(updatedItem))
    }
}
