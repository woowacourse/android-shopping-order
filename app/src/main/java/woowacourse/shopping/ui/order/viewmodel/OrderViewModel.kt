package woowacourse.shopping.ui.order.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.action.CartNavigationActions
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.cart.listener.CartClickListener
import woowacourse.shopping.ui.order.listener.OrderClickListener
import woowacourse.shopping.ui.order.recommend.listener.RecommendClickListener
import woowacourse.shopping.ui.state.OrderState
import woowacourse.shopping.ui.state.UiState

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(),
    CartClickListener,
    OrderClickListener,
    RecommendClickListener {
    // 공용
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
                if (cartUiState.value is UiState.Success && cartViewItemsValue.isNotEmpty()) {
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

    private val _orderState = MutableLiveData<OrderState>(OrderState.Cart)
    val orderState: LiveData<OrderState>
        get() = _orderState

    // Cart
    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

    private val _cartViewItems = MutableLiveData<List<CartViewItem>>()
    val cartViewItems: LiveData<List<CartViewItem>>
        get() = _cartViewItems

    private val _selectedCartViewItems = MutableLiveData<List<CartViewItem>>()
    val selectedCartViewItems: LiveData<List<CartViewItem>>
        get() = _selectedCartViewItems

    val isCartEmpty: LiveData<Boolean>
        get() =
            _cartViewItems.map { cartViewItemsValue ->
                cartViewItemsValue.isEmpty()
            }

    private val _cartNavigationActions = MutableLiveData<Event<CartNavigationActions>>()
    val cartNavigationActions: LiveData<Event<CartNavigationActions>>
        get() = _cartNavigationActions

    private val _cartNotifyingActions = MutableLiveData<Event<CartNotifyingActions>>()
    val cartNotifyingActions: LiveData<Event<CartNotifyingActions>>
        get() = _cartNotifyingActions

    // Recommend


    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadCartViewItems()
        }, 1000)
    }

    fun updateCartViewItems(newCartViewItems: List<CartViewItem>) {
        _cartViewItems.value = newCartViewItems
    }

    fun updateSelectedCartViewItems(newSelectedCatViewItems: List<CartViewItem>) {
        _selectedCartViewItems.value = newSelectedCatViewItems
    }

    private fun loadCartViewItems() {
        runCatching {
            val cartTotalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
            cartRepository.getCartItems(0, cartTotalQuantity, DESCENDING_SORT_ORDER)
        }.onSuccess {
            _cartViewItems.value =
                it.getOrNull()?.map(::CartViewItem)
            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
        }.onFailure {
            _cartUiState.value = UiState.Error(it)
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem ->
            cartViewItem.cartItem.product.productId == productId
        }
    }

    private fun getCartViewItemPosition(cartItemId: Int): Int? {
        return _cartViewItems.value?.indexOfFirst { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    override fun onCheckBoxClick(cartItemId: Int) {
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
        _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
    }

    override fun onCartItemClick(productId: Int) {
        _cartNavigationActions.value = Event(CartNavigationActions.NavigateToDetail(productId))
    }

    override fun onDeleteButtonClick(cartItemId: Int) {
        runCatching {
            cartRepository.deleteCartItem(cartItemId)
        }.onSuccess {
            val deletedCartViewItem = getCartViewItemByCartItemId(cartItemId) ?: return
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            newCartViewItems.remove(deletedCartViewItem)
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                _selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem -> selectedCartViewItem.cartItem.cartItemId == cartItemId }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems.removeAt(selectedPosition)
                _selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
            _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCartItemDeleted)
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
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

            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.minusQuantity())

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
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            if (updatedCartItem.cartItem.quantity == 0) {
                newCartViewItems.removeAt(position)
                _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCartItemDeleted)
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

            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
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
        _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
    }

    override fun onOrderButtonClick() {
        if (selectedCartViewItemSize.value == 0) {
            _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCanNotPutCart)
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

    override fun onProductClick(productId: Int) {
        _cartNavigationActions.value = Event(CartNavigationActions.NavigateToDetail(productId))
    }

    override fun onPlusButtonClick(product: Product) {
        var cartItemId = -1
        runCatching {
            cartItemId = cartRepository.addCartItem(product.productId, 1).getOrNull() ?: return
        }.onSuccess {
            val updatedCartItem = CartViewItem(CartItem(cartItemId, 1, product))
            _cartViewItems.value = _cartViewItems.value?.plus(updatedCartItem)
            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
        }
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
        const val DEFAULT_NUMBER_OF_RECOMMEND = 10
    }
}
