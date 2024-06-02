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
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.listener.CartClickListener
import woowacourse.shopping.view.cart.listener.CartItemClickListener
import woowacourse.shopping.view.cart.listener.QuantityClickListener
import woowacourse.shopping.view.cart.listener.RecommendClickListener
import woowacourse.shopping.view.home.adapter.product.HomeViewItem
import woowacourse.shopping.view.state.UiState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    CartItemClickListener,
    QuantityClickListener,
    CartClickListener,
    RecommendClickListener {
    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

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

    private val _cartViewItems = MutableLiveData<List<CartViewItem>>()
    val cartViewItems: LiveData<List<CartViewItem>>
        get() = _cartViewItems

    private val _recommendUiState =
        MutableLiveData<UiState<List<HomeViewItem.ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<HomeViewItem.ProductViewItem>>>
        get() = _recommendUiState

    private val _selectedCartViewItems = MutableLiveData<List<CartViewItem>>(mutableListOf())
    val selectedCartViewItems: LiveData<List<CartViewItem>>
        get() = _selectedCartViewItems

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
            _cartViewItems.value =
                cartResponse.getOrNull()?.cartItems?.map(::CartViewItem) ?: emptyList()
            _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
        }.onFailure {
            _cartUiState.value = UiState.Error(it)
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return _cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.product.productId == productId }
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
        _selectedCartViewItems.value = _selectedCartViewItems.value // Trigger LiveData update
        _cartUiState.value = UiState.Success(_cartViewItems.value ?: emptyList())
    }

    override fun onCartItemClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
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
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem -> selectedCartViewItem.cartItem.cartItemId == cartItemId }
                    ?: return
            val newSelectedCatViewItems = _selectedCartViewItems.value?.toMutableList() ?: return
            newSelectedCatViewItems.removeAt(selectedPosition)
            _selectedCartViewItems.value = newSelectedCatViewItems
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
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                _selectedCartViewItems.value = newSelectedCatViewItems
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
            val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            _cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    _selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                _selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
        }
    }

    override fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }

    override fun onAllCheckBoxClick() {
        val newCartViewItems = _cartViewItems.value?.toMutableList() ?: return
        if (allCheckBoxChecked.value == false) {
            _cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.check()
            }
            _selectedCartViewItems.value = cartViewItems.value
        } else {
            _cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.unCheck()
            }
            _selectedCartViewItems.value = emptyList()
        }
        _cartViewItems.value = newCartViewItems
        _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
    }

    override fun onOrderButtonClick() {
        TODO("Not yet implemented")
    }

    override fun onFinalOrderButtonClick() {
        TODO("Not yet implemented")
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
