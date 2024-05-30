package woowacourse.shopping.presentation.ui.cart

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.util.Event

class CartViewModel(private val cartRepository: CartRepository) : ViewModel(), CartHandler {
    private val _error = MutableLiveData<Event<CartError>>()

    val error: LiveData<Event<CartError>> = _error

    private var orderState: OrderState = OrderState.CartList

    private val _orderEvent = MutableLiveData<Event<OrderEvent>>()

    val orderEvent: LiveData<Event<OrderEvent>> = _orderEvent

    private val _shoppingProducts =
        MutableLiveData<UiState<List<ProductListItem.ShoppingProductItem>>>(UiState.Loading)

    val shoppingProducts: LiveData<UiState<List<ProductListItem.ShoppingProductItem>>> get() = _shoppingProducts

    private val _changedCartProducts = MutableLiveData<List<Cart>>()
    val changedCartProducts: LiveData<List<Cart>> get() = _changedCartProducts

    val isAllCartItemsSelected: LiveData<Boolean> =
        shoppingProducts.switchMap {
            MutableLiveData(
                if (it is UiState.Success && it.data.isNotEmpty()) {
                    it.data.all { it.isChecked }
                } else {
                    false
                },
            )
        }

    private val selectedCartItems: LiveData<List<ProductListItem.ShoppingProductItem>> =
        shoppingProducts.switchMap {
            MutableLiveData(
                if (it is UiState.Success) {
                    it.data.filter { it.isChecked }
                } else {
                    emptyList()
                },
            )
        }

    val totalPrice: LiveData<Long> =
        selectedCartItems.switchMap {
            MutableLiveData(
                it.sumOf { it.price * it.quantity },
            )
        }

    val totalCount: LiveData<Int> =
        selectedCartItems.switchMap {
            MutableLiveData(
                it.sumOf { it.quantity },
            )
        }

    fun setOrderState(state: OrderState) {
        orderState = state
    }

    fun loadAllCartItems(pageSize: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            cartRepository.load(0, pageSize, onSuccess = { carts, _ ->
                _shoppingProducts.value = UiState.Success(carts.map { it.toShoppingProduct() })
            }, onFailure = { _error.value = Event(CartError.CartItemsNotFound) })
        }, 500)
    }

    override fun onDeleteClick(product: ProductListItem.ShoppingProductItem) {
        cartRepository.deleteExistCartItem(product.cartId, onSuccess = { cartId, newQuantity ->
            val originalChangedCartProducts = changedCartProducts.value ?: emptyList()
            _changedCartProducts.value =
                originalChangedCartProducts.plus(Cart(cartId, product.toProduct(), newQuantity))
            val newShoppingProduct =
                (shoppingProducts.value as UiState.Success<List<ProductListItem.ShoppingProductItem>>).data.filter { it.id != product.id }
            _shoppingProducts.value = UiState.Success(newShoppingProduct)
        }, onFailure = { _error.value = Event(CartError.CartItemNotDeleted) })
    }

    override fun onCheckBoxClicked(product: ProductListItem.ShoppingProductItem) {
        if (shoppingProducts.value is UiState.Success) {
            val cartItems =
                (shoppingProducts.value as UiState.Success<List<ProductListItem.ShoppingProductItem>>).data

            val updatedCartItems =
                cartItems.map {
                    if (it.id == product.id) {
                        it.copy(isChecked = !product.isChecked)
                    } else {
                        it
                    }
                }
            _shoppingProducts.value = UiState.Success(updatedCartItems)
        }
    }

    override fun onTotalCheckBoxClicked(isChecked: Boolean) {
        if (shoppingProducts.value is UiState.Success) {
            val cart =
                shoppingProducts.value as UiState.Success<List<ProductListItem.ShoppingProductItem>>
            _shoppingProducts.value =
                UiState.Success(cart.data.map { it.copy(isChecked = isChecked) })
        }
    }

    override fun onOrderClicked() {
        Log.d("ㅌㅅㅌ", "onOrderClicked : $orderState")
        when (orderState) {
            is OrderState.CartList -> {
                if (totalCount.value != 0) {
                    _orderEvent.value = Event(OrderEvent.MoveToRecommend)
                }
            }

            is OrderState.Recommend -> {
                _orderEvent.value = Event(OrderEvent.CompleteOrder)
            }
        }
    }

    private fun modifyShoppingProductQuantity(
        cartItem: ProductListItem.ShoppingProductItem,
        resultQuantity: Int,
    ) {
        val newShoppingProducts =
            (shoppingProducts.value as UiState.Success<List<ProductListItem.ShoppingProductItem>>).data.map {
                if (it.cartId == cartItem.cartId) {
                    it.copy(quantity = resultQuantity, isChecked = cartItem.isChecked)
                } else {
                    it
                }
            }
        _shoppingProducts.value = UiState.Success(newShoppingProducts)
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
                        modifyShoppingProductQuantity(item, resultQuantity)
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
                onSuccess = { _, resultQuantity ->
                    modifyShoppingProductQuantity(item, resultQuantity)
                },
                onFailure = {},
            )
        }
    }
}
