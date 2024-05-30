package woowacourse.shopping.presentation.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.ProductListItem.ShoppingProductItem.Companion.joinProductAndCart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.util.Event

class CartViewModel(private val cartRepository: CartRepository) : ViewModel(), CartHandler {

    private val _error = MutableLiveData<Event<CartError>>()

    val error: LiveData<Event<CartError>> = _error

    private val _shoppingProducts =
        MutableLiveData<UiState<List<ProductListItem.ShoppingProductItem>>>(UiState.Loading)

    val shoppingProducts: LiveData<UiState<List<ProductListItem.ShoppingProductItem>>> get() = _shoppingProducts

    private val cartProducts = mutableListOf<Cart>()

    private val _changedCartProducts = MutableLiveData<List<Cart>>()
    val changedCartProducts: LiveData<List<Cart>> get() = _changedCartProducts

    fun loadProductByPage(pageSize: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            cartRepository.load(0, pageSize, onSuccess = { carts, totalPage ->
                cartProducts.apply {
                    clear()
                    addAll(carts)
                }
                _shoppingProducts.value =
                    UiState.Success(
                        cartProducts.map {
                            joinProductAndCart(
                                it.product,
                                it,
                            )
                        },
                    )
            }, onFailure = { _error.value = Event(CartError.CartItemsNotFound) })
        }, 500)
    }

    override fun onDeleteClick(product: ProductListItem.ShoppingProductItem) {
        cartRepository.deleteExistCartItem(product.cartId, onSuccess = { cartId, newQuantity ->
            val originalChangedCartProducts = changedCartProducts.value ?: emptyList()
            _changedCartProducts.value =
                originalChangedCartProducts.plus(Cart(cartId, product.toProduct(), newQuantity))
        }, onFailure = { _error.value = Event(CartError.CartItemNotDeleted) })
    }

    override fun onCheckBoxClicked(product: ProductListItem.ShoppingProductItem) {
        product.isChecked = !product.isChecked
    }

    private fun modifyShoppingProductQuantity(
        cartId: Long,
        resultQuantity: Int,
    ) {
        val productIndex = cartProducts.indexOfFirst { it.cartId == cartId }
        val updatedItem = cartProducts[productIndex].copy(quantity = resultQuantity)
        cartProducts[productIndex] = updatedItem
        _shoppingProducts.value =
            UiState.Success(
                cartProducts.map {
                    joinProductAndCart(
                        it.product,
                        it,
                    )
                },
            )
        val originalChangedCartProducts = changedCartProducts.value ?: emptyList()
        _changedCartProducts.value = originalChangedCartProducts.plus(updatedItem)
    }

    override fun onDecreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        val updatedQuantity = item?.let { it.quantity - 1 } ?: 1
        if (updatedQuantity > 0) {
            item?.let { item ->
                cartRepository.updateDecrementQuantity(
                    item.cartId,
                    item.id,
                    1,
                    item.quantity,
                    onSuccess = { _, resultQuantity ->
                        modifyShoppingProductQuantity(item.cartId, resultQuantity)
                    },
                    onFailure = {},
                )
            }
        }
    }

    override fun onIncreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        item?.let { item ->
            cartRepository.updateIncrementQuantity(
                item.cartId,
                item.id,
                1,
                item.quantity,
                onSuccess = { cartId, resultQuantity ->
                    modifyShoppingProductQuantity(item.cartId, resultQuantity)
                },
                onFailure = {},
            )
        }
    }
}
