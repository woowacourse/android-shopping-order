package woowacourse.shopping.ui.order.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.listener.OrderClickListener
import woowacourse.shopping.ui.state.OrderState

class OrderViewModel(private val cartRepository: CartRepository) : ViewModel(), OrderClickListener {
    private val _orderState = MutableLiveData<OrderState>(OrderState.Cart)
    val orderState: LiveData<OrderState>
        get() = _orderState

    private val _cartViewItems = MutableLiveData<List<CartViewItem>>(emptyList())
    val cartViewItems: LiveData<List<CartViewItem>>
        get() = _cartViewItems

    val totalPrice: LiveData<Int>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                val selectedCartViewItems =
                    cartViewItemsValue.filter { cartViewItem -> cartViewItem.isChecked }
                selectedCartViewItems.sumOf { cartViewItem ->
                    cartViewItem.cartItem.totalPrice
                }
            }

    val selectedCartViewItemSize: LiveData<Int>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                val selectedCartViewItems =
                    cartViewItemsValue.filter { cartViewItem -> cartViewItem.isChecked }
                selectedCartViewItems.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.quantity
                }
            }

    val allCheckBoxChecked: LiveData<Boolean>
        get() =
            _cartViewItems.map { cartViewItemsValue ->
                if (cartViewItemsValue != null && cartViewItemsValue.isNotEmpty()) {
                    cartViewItemsValue.all { selectedCartViewItem -> selectedCartViewItem.isChecked }
                } else {
                    false
                }
            }

    private val _orderNavigationActions = MutableLiveData<Event<OrderNavigationActions>>()
    val orderNavigationActions: LiveData<Event<OrderNavigationActions>>
        get() = _orderNavigationActions

    private val _orderNotifyingActions = MutableLiveData<Event<OrderNotifyingActions>>()
    val orderNotifyingActions: LiveData<Event<OrderNotifyingActions>>
        get() = _orderNotifyingActions

    fun updateCartViewItems(newCartViewItems: List<CartViewItem>) {
        _cartViewItems.value = newCartViewItems
    }

    private fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem ->
            cartViewItem.cartItem.product.productId == productId
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemPosition(cartItemId: Int): Int? {
        return cartViewItems.value?.indexOfFirst { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    fun onCheckBoxClick(cartItemId: Int) {
        var updatedCartItem = getCartViewItemByCartItemId(cartItemId) ?: return
        updatedCartItem = updatedCartItem.toggleCheck()

        val position = getCartViewItemPosition(cartItemId) ?: return
        val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
        newCartViewItems[position] = updatedCartItem
        _cartViewItems.value = newCartViewItems
    }

    fun onDeleteButtonClick(cartItemId: Int) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(cartItemId)
                .onSuccess {
                    val deletedCartViewItem =
                        getCartViewItemByCartItemId(cartItemId) ?: return@onSuccess
                    val newCartViewItems = cartViewItems.value?.toMutableList() ?: return@onSuccess
                    newCartViewItems.remove(deletedCartViewItem)
                    _cartViewItems.value = newCartViewItems
                }.onFailure {
                    _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyError)
                }
        }
    }

    fun onQuantityPlusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.plusQuantity())

        viewModelScope.launch {
            cartRepository.updateCartItem(
                updatedCartItem.cartItem.cartItemId,
                updatedCartItem.cartItem.quantity,
            ).onSuccess {
                val position =
                    getCartViewItemPosition(updatedCartItem.cartItem.cartItemId)
                        ?: return@onSuccess
                val newCartViewItems = cartViewItems.value?.toMutableList() ?: return@onSuccess
                newCartViewItems[position] = updatedCartItem
                _cartViewItems.value = newCartViewItems
            }.onFailure { _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyError) }
        }
    }

    fun onQuantityMinusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.minusQuantity())
        val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return

        viewModelScope.launch {
            if (updatedCartItem.cartItem.quantity == 0) {
                cartRepository.deleteCartItem(updatedCartItem.cartItem.cartItemId)
            } else {
                cartRepository.updateCartItem(
                    updatedCartItem.cartItem.cartItemId,
                    updatedCartItem.cartItem.quantity,
                )
            }
                .onSuccess {
                    val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return@onSuccess
                    if (updatedCartItem.cartItem.quantity == 0) {
                        newCartViewItems.removeAt(position)
                    } else {
                        newCartViewItems[position] = updatedCartItem
                    }
                    _cartViewItems.value = newCartViewItems
                }.onFailure {
                    _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyError)
                }
        }
    }

    override fun onBackButtonClick() {
        _orderNavigationActions.value = Event(OrderNavigationActions.NavigateToBack)
    }

    override fun onAllCheckBoxClick() {
        val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
        if (allCheckBoxChecked.value == false) {
            _cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.check()
            }
        } else {
            _cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.unCheck()
            }
        }
        _cartViewItems.value = newCartViewItems
    }

    override fun onOrderButtonClick() {
        if (selectedCartViewItemSize.value == 0) {
            _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyCanNotOrder)
        } else {
            val orderState = orderState.value ?: return
            when (orderState) {
                is OrderState.Cart -> {
                    _orderState.value = OrderState.Recommend
                    _orderNavigationActions.value =
                        Event(OrderNavigationActions.NavigateToRecommend)
                }

                is OrderState.Recommend -> {
                    _orderNavigationActions.value = Event(OrderNavigationActions.NavigateToPayment)
                }
            }
        }
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
        const val DEFAULT_NUMBER_OF_RECOMMEND = 10
    }
}
