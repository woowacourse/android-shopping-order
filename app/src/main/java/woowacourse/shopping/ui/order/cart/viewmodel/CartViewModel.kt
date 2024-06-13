package woowacourse.shopping.ui.order.cart.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.order.cart.action.CartNavigationActions
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions
import woowacourse.shopping.ui.order.cart.action.CartShareActions
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.cart.listener.CartClickListener
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel(), CartClickListener {
    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

    private var sharedCartViewItems: List<CartViewItem> = emptyList()

    private val _isCartEmpty = MutableLiveData<Boolean>()
    val isCartEmpty: LiveData<Boolean>
        get() = _isCartEmpty

    private val _cartShareActions = MutableLiveData<Event<CartShareActions>>()
    val cartShareActions: LiveData<Event<CartShareActions>>
        get() = _cartShareActions

    private val _cartNavigationActions = MutableLiveData<Event<CartNavigationActions>>()
    val cartNavigationActions: LiveData<Event<CartNavigationActions>>
        get() = _cartNavigationActions

    private val _cartNotifyingActions = MutableLiveData<Event<CartNotifyingActions>>()
    val cartNotifyingActions: LiveData<Event<CartNotifyingActions>>
        get() = _cartNotifyingActions

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadCartViewItems()
        }, 1000)
    }

    fun updateCartUiState() {
        if (cartUiState.value is UiState.Success) {
            _cartUiState.value =
                UiState.Success(sharedCartViewItems)
        }
    }

    fun loadCartViewItems() {
        viewModelScope.launch {
            val cartTotalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
            cartRepository.getCartItems(0, cartTotalQuantity, OrderViewModel.DESCENDING_SORT_ORDER)
                .onSuccess { cartItems ->
                    val cartViewItems = cartItems.map(::CartViewItem)
                    _cartShareActions.value =
                        Event(CartShareActions.UpdateNewCartViewItems(cartViewItems))
                    _cartUiState.value = UiState.Success(sharedCartViewItems)
                }.onFailure {
                    _cartUiState.value = UiState.Error(it)
                }
        }
    }

    fun updateSharedCartViewItems(cartViewItems: List<CartViewItem>) {
        this.sharedCartViewItems = cartViewItems
    }

    fun updateIsCartEmpty(isCartEmpty: Boolean) {
        _isCartEmpty.value = isCartEmpty
    }

    override fun onCheckBoxClick(cartItemId: Int) {
        _cartShareActions.value = Event(CartShareActions.CheckCartViewItem(cartItemId))

        _cartUiState.value = UiState.Success(sharedCartViewItems)
    }

    override fun onCartItemClick(productId: Int) {
        _cartNavigationActions.value = Event(CartNavigationActions.NavigateToDetail(productId))
    }

    override fun onDeleteButtonClick(cartItemId: Int) {
        _cartShareActions.value = Event(CartShareActions.DeleteCartViewItem(cartItemId))
        _cartUiState.value = UiState.Success(sharedCartViewItems)
        _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCartItemDeleted)
    }

    override fun onPlusButtonClick(product: Product) {
        viewModelScope.launch {
            cartRepository.addCartItem(product.productId, 1)
                .onSuccess { cartItemId ->
                    val updatedCartViewItem = CartViewItem(CartItem(cartItemId, 1, product))
                    val newCartViewItems = sharedCartViewItems.toMutableList()
                    newCartViewItems.add(updatedCartViewItem)

                    _cartShareActions.value =
                        Event(CartShareActions.UpdateNewCartViewItems(newCartViewItems))
                    _cartUiState.value = UiState.Success(sharedCartViewItems)
                }
                .onFailure { _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyError) }
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        _cartShareActions.value = Event(CartShareActions.PlusCartViewItemQuantity(productId))

        _cartUiState.value = UiState.Success(sharedCartViewItems)
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val updatedCartItem =
            sharedCartViewItems.firstOrNull { cartViewItem ->
                cartViewItem.cartItem.product.productId == productId
            } ?: return

        if (updatedCartItem.cartItem.quantity > 1) {
            _cartShareActions.value =
                Event(CartShareActions.MinusCartViewItemQuantity(productId))
        }
        _cartUiState.value = UiState.Success(sharedCartViewItems)
    }
}
