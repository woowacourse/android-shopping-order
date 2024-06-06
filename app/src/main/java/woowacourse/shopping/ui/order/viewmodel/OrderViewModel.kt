package woowacourse.shopping.ui.order.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.listener.OrderClickListener
import woowacourse.shopping.ui.state.OrderState

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), OrderClickListener {
    // 공용
    private val _orderState = MutableLiveData<OrderState>(OrderState.Cart)
    val orderState: LiveData<OrderState>
        get() = _orderState

    private val _cartViewItems = MutableLiveData<List<CartViewItem>>()
    val cartViewItems: LiveData<List<CartViewItem>>
        get() = _cartViewItems

    private val _selectedCartViewItems = MutableLiveData<List<CartViewItem>>(emptyList())
    val selectedCartViewItems: LiveData<List<CartViewItem>>
        get() = _selectedCartViewItems

    val totalPrice: LiveData<Int>
        get() =
            _selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.totalPrice
                }
            }

    val selectedCartViewItemSize: LiveData<Int>
        get() =
            _selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
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

    fun updateSelectedCartViewItems(newSelectedCatViewItems: List<CartViewItem>) {
        _selectedCartViewItems.value = newSelectedCatViewItems
    }

    fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem ->
            cartViewItem.cartItem.product.productId == productId
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemPosition(cartItemId: Int): Int? {
        return _cartViewItems.value?.indexOfFirst { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    fun onCheckBoxClick(cartItemId: Int) {
        var updatedCartItem = getCartViewItemByCartItemId(cartItemId) ?: return
        updatedCartItem = updatedCartItem.toggleCheck()

        if (updatedCartItem.isChecked) {
            _selectedCartViewItems.value = _selectedCartViewItems.value?.plus(updatedCartItem)
        } else {
            _selectedCartViewItems.value =
                _selectedCartViewItems.value?.filter { it.cartItem.cartItemId != cartItemId }
        }

        val position = getCartViewItemPosition(cartItemId) ?: return
        val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
        newCartViewItems[position] = updatedCartItem
        _cartViewItems.value = newCartViewItems
        _selectedCartViewItems.value = _selectedCartViewItems.value
    }

    fun onDeleteButtonClick(cartItemId: Int) {
        runCatching {
            cartRepository.deleteCartItem(cartItemId)
        }.onSuccess {
            val deletedCartViewItem = getCartViewItemByCartItemId(cartItemId) ?: return
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            newCartViewItems.remove(deletedCartViewItem)
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                _selectedCartViewItems.value?.indexOfFirst {
                        selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems.removeAt(selectedPosition)
                _selectedCartViewItems.value = newSelectedCatViewItems
            }
        }
    }

    fun onQuantityPlusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.plusQuantity())

        runCatching {
            cartRepository.updateCartItem(
                updatedCartItem.cartItem.cartItemId,
                updatedCartItem.cartItem.quantity,
            )
        }.onSuccess {
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                _selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                } ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                _selectedCartViewItems.value = newSelectedCatViewItems
            }
        }
    }

    fun onQuantityMinusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.minusQuantity())
        val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return

        runCatching {
            if (updatedCartItem.cartItem.quantity == 0) {
                cartRepository.deleteCartItem(updatedCartItem.cartItem.cartItemId)
            } else {
                cartRepository.updateCartItem(
                    updatedCartItem.cartItem.cartItemId,
                    updatedCartItem.cartItem.quantity,
                )
            }
        }.onSuccess {
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            if (updatedCartItem.cartItem.quantity == 0) {
                newCartViewItems.removeAt(position)
            } else {
                newCartViewItems[position] = updatedCartItem
            }
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                _selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                _selectedCartViewItems.value = newSelectedCatViewItems
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
            _selectedCartViewItems.value = _cartViewItems.value
        } else {
            _cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.unCheck()
            }
            _selectedCartViewItems.value = emptyList()
        }
        _cartViewItems.value = newCartViewItems
    }

    override fun onOrderButtonClick() {
        if (selectedCartViewItemSize.value == 0) {
            _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyCanNotOrder)
        } else {
            if (orderState.value is OrderState.Cart) {
                _orderState.value = OrderState.Recommend
                _orderNavigationActions.value = Event(OrderNavigationActions.NavigateToRecommend)
            } else {
                runCatching {
                    val selectedCartItemIds =
                        _selectedCartViewItems.value?.map { selectedCartViewItem ->
                            selectedCartViewItem.cartItem.cartItemId
                        } ?: return
                    orderRepository.postOrder(selectedCartItemIds)
                }.onSuccess {
                    _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyCartCompleted)
                }
            }
        }
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
        const val DEFAULT_NUMBER_OF_RECOMMEND = 10
    }
}
