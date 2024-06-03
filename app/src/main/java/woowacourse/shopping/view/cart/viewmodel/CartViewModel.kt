package woowacourse.shopping.view.cart.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.listener.CartClickListener
import woowacourse.shopping.view.cart.listener.CartItemClickListener
import woowacourse.shopping.view.cart.listener.RecommendClickListener
import woowacourse.shopping.view.event.Event
import woowacourse.shopping.view.home.adapter.product.HomeViewItem
import woowacourse.shopping.view.state.OrderState
import woowacourse.shopping.view.state.UiState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    CartItemClickListener,
    CartClickListener,
    RecommendClickListener {
    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

    private val cartViewItems = MutableLiveData<List<CartViewItem>>()

    val isCartEmpty: LiveData<Boolean>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                cartViewItemsValue.isEmpty()
            }

    val totalPrice: LiveData<Int>
        get() =
            selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.totalPrice
                }
            }

    private val _recommendUiState =
        MutableLiveData<UiState<List<HomeViewItem.ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<HomeViewItem.ProductViewItem>>>
        get() = _recommendUiState

    private val selectedCartViewItems = MutableLiveData<List<CartViewItem>>(mutableListOf())

    val selectedCartViewItemSize: LiveData<Int>
        get() =
            selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.quantity
                }
            }

    private val _orderState = MutableLiveData<OrderState>(OrderState.Cart)
    val orderState: LiveData<OrderState>
        get() = _orderState

    val allCheckBoxChecked: LiveData<Boolean>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                if (cartUiState.value is UiState.Success && cartViewItemsValue.isNotEmpty()) {
                    cartViewItemsValue.all { selectedCartViewItem -> selectedCartViewItem.isChecked }
                } else {
                    false
                }
            }

    private val _isBackButtonClicked = MutableLiveData<Event<Boolean>>()
    val isBackButtonClicked: LiveData<Event<Boolean>>
        get() = _isBackButtonClicked

    private val _navigateToDetail = MutableLiveData<Event<Int>>()
    val navigateToDetail: LiveData<Event<Int>>
        get() = _navigateToDetail

    private val _navigateToRecommend = MutableLiveData<Event<Boolean>>()
    val navigateToRecommend: LiveData<Event<Boolean>>
        get() = _navigateToRecommend

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>>
        get() = _navigateToBack

    private val _notifyDeletion = MutableLiveData<Event<Boolean>>()
    val notifyDeletion: LiveData<Event<Boolean>>
        get() = _notifyDeletion

    private val _notifyCanNotOrder = MutableLiveData<Event<Boolean>>()
    val notifyCanNotOrder: LiveData<Event<Boolean>>
        get() = _notifyCanNotOrder

    private val _notifyOrderCompleted = MutableLiveData<Event<Boolean>>()
    val notifyOrderCompleted: LiveData<Event<Boolean>>
        get() = _notifyOrderCompleted

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadCartViewItems()
        }, 1000)
    }

    private fun loadCartViewItems() {
        runCatching {
            val cartTotalQuantity = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
            cartRepository.getCartResponse(0, cartTotalQuantity, DESCENDING_SORT_ORDER)
        }.onSuccess { cartResponse ->
            cartViewItems.value =
                cartResponse.getOrNull()?.cartItems?.map(::CartViewItem) ?: emptyList()
            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
        }.onFailure {
            _cartUiState.value = UiState.Error(it)
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.product.productId == productId }
    }

    private fun getCartViewItemPosition(cartItemId: Int): Int? {
        return cartViewItems.value?.indexOfFirst { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    override fun onCheckBoxClick(cartItemId: Int) {
        var updatedCartItem = getCartViewItemByCartItemId(cartItemId) ?: return
        updatedCartItem = updatedCartItem.toggleCheck()

        if (updatedCartItem.isChecked) {
            selectedCartViewItems.value = selectedCartViewItems.value?.plus(updatedCartItem)
        } else {
            selectedCartViewItems.value =
                selectedCartViewItems.value?.filter { it.cartItem.cartItemId != cartItemId }
        }

        val position = getCartViewItemPosition(cartItemId) ?: return
        val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
        newCartViewItems[position] = updatedCartItem
        cartViewItems.value = newCartViewItems
        selectedCartViewItems.value = selectedCartViewItems.value // Trigger LiveData update
        _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
    }

    override fun onCartItemClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onDeleteButtonClick(cartItemId: Int) {
        runCatching {
            cartRepository.deleteCartItem(cartItemId)
        }.onSuccess {
            val deletedCartViewItem = getCartViewItemByCartItemId(cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems.remove(deletedCartViewItem)
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem -> selectedCartViewItem.cartItem.cartItemId == cartItemId }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems.removeAt(selectedPosition)
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
            _notifyDeletion.value = Event(true)
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
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.minusQuantity())

        runCatching {
            cartRepository.updateCartItem(
                updatedCartItem.cartItem.cartItemId,
                updatedCartItem.cartItem.quantity,
            )
        }.onSuccess {
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
        }
    }

    override fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }

    override fun onAllCheckBoxClick() {
        val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
        if (allCheckBoxChecked.value == false) {
            cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.check()
            }
            selectedCartViewItems.value = cartViewItems.value
        } else {
            cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.unCheck()
            }
            selectedCartViewItems.value = emptyList()
        }
        cartViewItems.value = newCartViewItems
        _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
    }

    override fun onOrderButtonClick() {
        if (selectedCartViewItemSize.value == 0) {
            _notifyCanNotOrder.value = Event(true)
        } else {
            if (orderState.value is OrderState.Cart) {
                _orderState.value = OrderState.Recommend
                _navigateToRecommend.value = Event(true)
            } else {
                runCatching {
                    val selectedCartItemIds =
                        selectedCartViewItems.value?.map { selectedCartViewItem ->
                            selectedCartViewItem.cartItem.cartItemId
                        } ?: return
                    orderRepository.postOrder(selectedCartItemIds)
                }.onSuccess {
                    _notifyOrderCompleted.value = Event(true)
                }
            }
        }
    }

    override fun onProductClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onPlusButtonClick(product: Product) {
        cartRepository.addCartItem(product.productId, 1).getOrNull()
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
    }
}
