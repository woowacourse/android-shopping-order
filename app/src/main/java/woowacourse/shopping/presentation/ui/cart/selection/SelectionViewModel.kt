package woowacourse.shopping.presentation.ui.cart.selection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.ShoppingCart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class SelectionViewModel(
    private val cartRepository: CartRepository,
    private val orderDatabase: OrderDatabase,
) : ViewModel(),
    SelectionEventHandler,
    CounterHandler {
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _cartItems: MutableLiveData<ShoppingCart> =
        MutableLiveData<ShoppingCart>().apply {
            viewModelScope.launch {
                value = ShoppingCart(fetchCartItems())
            }
        }
    val cartItems: LiveData<ShoppingCart>
        get() = _cartItems

    val cartItemsState: LiveData<UIState<ShoppingCart>> =
        cartItems.switchMap { shoppingCart ->
            if (shoppingCart.items.isEmpty()) {
                MutableLiveData(UIState.Empty)
            } else {
                MutableLiveData(UIState.Success(shoppingCart))
            }
        }

    private val _isEmpty = MutableLiveData<Boolean>(false)

    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val _navigateToDetail = MutableLiveData<Event<Long>>()

    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail
    private val _deleteCartItem = MutableLiveData<Event<Long>>()

    val deleteCartItem: LiveData<Event<Long>>
        get() = _deleteCartItem

    private val _totalPrice = MutableLiveData<Long>(cartItems.value?.getTotalPrice() ?: 0L)
    val totalPrice: LiveData<Long>
        get() = _totalPrice

    private val _totalQuantity = MutableLiveData<Int>(cartItems.value?.getTotalQuantity() ?: 0)
    val totalQuantity: LiveData<Int>
        get() = _totalQuantity

    fun updateCartItems() {
        viewModelScope.launch {
            _cartItems.value = ShoppingCart(fetchCartItems())
        }
    }

    private suspend fun fetchCartItems(): List<CartItem> {
        var items = emptyList<CartItem>()
        val transaction =
            viewModelScope.async {
                cartRepository.findAll()
            }.await()

        transaction.onSuccess {
            items = it.items

            // To check the shimmer UI is working properly
            delay(1000)
        }
        onLoaded()
        return items
    }

    fun isCartEmpty() {
        _isEmpty.postValue(true)
    }

    fun onLoading() {
        _isLoading.value = (true)
    }

    fun onLoaded() {
        _isLoading.value = (false)
    }

    override fun onItemClicked(itemId: Long) {
        val shoppingCart = cartItems.value ?: return
        viewModelScope.launch {
            if (shoppingCart.items.find { it.id == itemId }?.isChecked == true) {
                removeFromOrder(shoppingCart.items.find { it.id == itemId }!!)
            } else {
                addToOrder(shoppingCart.items.find { it.id == itemId }!!)
            }

            // To notify the change of order
            _cartItems.value = _cartItems.value
        }
        updatePriceAndQuantity()
    }

    private fun updatePriceAndQuantity() {
        _totalPrice.value = cartItems.value?.getTotalPrice() ?: 0L
        _totalQuantity.value = cartItems.value?.getTotalQuantity() ?: 0
    }

    private fun removeFromOrder(cartItem: CartItem) {
        val shoppingCart = cartItems.value?.copy() ?: return
        shoppingCart.items.find { it.id == cartItem.id }?.isChecked = false
    }

    private fun addToOrder(cartItem: CartItem) {
        val shoppingCart = cartItems.value?.copy() ?: return
        shoppingCart.items.find { it.id == cartItem.id }?.isChecked = true
    }

    override fun deleteCartItem(itemId: Long) {
        _deleteCartItem.postValue(Event(itemId))
    }

    fun deleteItem(itemId: Long) {
        val shoppingCart = cartItems.value?.copy() ?: return
        val items = shoppingCart.items.toMutableList()
        items.removeIf { it.id == itemId }
        _cartItems.value = ShoppingCart(items)

        viewModelScope.launch {
            cartRepository.delete(itemId)
        }
    }

    override fun increaseCount(productId: Long) {
        viewModelScope.launch {
            val currentQuantity = cartRepository.findQuantityWithProductId(productId)
            currentQuantity.onSuccess {
                cartRepository.updateQuantityWithProductId(productId, it + 1)
            }
            val shoppingCart = cartItems.value ?: return@launch
            val index = shoppingCart.items.indexOfFirst { it.productId == productId } ?: return@launch
            val newItem = shoppingCart.items[index].copy(quantity = shoppingCart.items[index].quantity.plus(1))
            val currentCartItems = shoppingCart.items.toMutableList() ?: return@launch
            currentCartItems.removeIf { it.productId == productId }
            currentCartItems.add(index, newItem ?: return@launch)

            _cartItems.value = ShoppingCart(currentCartItems)
            updatePriceAndQuantity()
        }
    }

    override fun decreaseCount(productId: Long) {
        viewModelScope.launch {
            val currentQuantity = cartRepository.findQuantityWithProductId(productId)
            currentQuantity.onSuccess {
                if (it > 1) {
                    cartRepository.updateQuantityWithProductId(productId, it - 1)

                    val shoppingCart = cartItems.value ?: return@launch
                    val index = shoppingCart.items.indexOfFirst { it.productId == productId } ?: return@launch
                    val newItem = shoppingCart.items[index].copy(quantity = shoppingCart.items[index].quantity.minus(1))
                    val currentCartItems = shoppingCart.items.toMutableList() ?: return@launch
                    currentCartItems.removeIf { it.productId == productId }
                    currentCartItems.add(index, newItem ?: return@launch)

                    _cartItems.value = ShoppingCart(currentCartItems)
                }
                updatePriceAndQuantity()
            }
        }
    }

    fun selectAllByCondition() {
        val shoppingCart = cartItems.value?.copy() ?: return
        val size = shoppingCart.items.size
        val checkedCount = shoppingCart.items.filter { it.isChecked }.size

        if (size == checkedCount) {
            unSelectAll()
        } else {
            selectAll()
        }
    }

    private fun unSelectAll() {
        val shoppingCart = cartItems.value?.copy() ?: return
        shoppingCart.unSelectAll()
        _cartItems.value = shoppingCart

        updatePriceAndQuantity()
    }

    private fun selectAll() {
        val shoppingCart = cartItems.value?.copy() ?: return
        shoppingCart.selectAll()
        _cartItems.value = shoppingCart

        updatePriceAndQuantity()
    }

    fun postOrder() {
        val shoppingCart = cartItems.value ?: return
        val order = shoppingCart.toOrder()
        orderDatabase.postOrder(order)
    }
}
