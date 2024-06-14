package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.ui.cart.event.ShoppingCartError
import woowacourse.shopping.ui.cart.event.ShoppingCartEvent
import woowacourse.shopping.ui.cart.listener.ShoppingCartListener
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.model.toOrderItem
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ShoppingCartViewModel(
    private val cartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(),
    ShoppingCartListener {
    private var _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private var _deletedItemId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val deletedItemId: SingleLiveData<Long> get() = _deletedItemId

    private var _isAllSelected = MutableLiveData(false)
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private var _selectedCartItemsTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedCartItemsTotalPrice: LiveData<Int> get() = _selectedCartItemsTotalPrice

    private var _selectedCartItemsCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedCartItemsCount: LiveData<Int> get() = _selectedCartItemsCount

    private val _event: MutableSingleLiveData<ShoppingCartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ShoppingCartEvent> get() = _event

    private val _error: MutableSingleLiveData<ShoppingCartError> = MutableSingleLiveData()
    val error: SingleLiveData<ShoppingCartError> get() = _error

    fun loadAll() {
        viewModelScope.launch {
            updateCartItems()
        }
    }

    fun deleteItem(cartItemId: Long) {
        viewModelScope.launch {
            cartRepository.removeShoppingCartProduct(cartItemId)
                .onSuccess {
                    _event.setValue(ShoppingCartEvent.DeleteCartItem)
                }
                .onFailure {
                    _error.setValue(ShoppingCartError.DeleteCartItem)
                }
            updateCartItems()
        }
    }

    private fun updateSelectedCartItemsCount() {
        _selectedCartItemsCount.value =
            cartItems.value.orEmpty().asSequence()
                .filter(CartItem::checked)
                .sumOf(CartItem::quantity)

        if (cartItems.value.isNullOrEmpty()) {
            _isAllSelected.value = false
        }
    }

    override fun navigateToOrder() {
        if (selectedCartItemsCount.value == 0) {
            _error.setValue(ShoppingCartError.EmptyOrderProduct)
            return
        }

        val orderItems =
            cartItems.value.orEmpty()
                .asSequence()
                .filter(CartItem::checked)
                .map(CartItem::toOrderItem)

        viewModelScope.launch {
            orderRepository.save(orderItems.toList())
                .onSuccess {
                    _event.setValue(ShoppingCartEvent.NavigationOrder)
                }
                .onFailure {
                    _error.setValue(ShoppingCartError.SaveOrderItems)
                }
        }
    }

    override fun onRemove(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository.addShoppingCartProduct(productId, INCREASE_AMOUNT)
                .onSuccess {
                    updateCartItems()
                }
                .onFailure {
                    _error.setValue(ShoppingCartError.UpdateCartItems)
                }
        }
    }

    private suspend fun updateCartItems() {
        cartRepository.loadAllCartItems()
            .onSuccess { cartItems ->
                updateCartItems(cartItems)
                updateTotalPrice()
                updateSelectedCartItemsCount()
            }
            .onFailure {
                _error.setValue(ShoppingCartError.LoadCartProducts)
            }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        val cart =
            cartItems.value.orEmpty().find { cartItem ->
                cartItem.product.id == productId
            } ?: return

        viewModelScope.launch {
            cartRepository.updateProductQuantity(cart.id, quantity)
                .onSuccess {
                    updateCartItems()
                }
                .onFailure {
                    _error.setValue(ShoppingCartError.UpdateCartItems)
                }
        }
    }

    private fun updateCartItems(currentItems: List<CartItem>) {
        val existingCartItems = _cartItems.value.orEmpty()

        _cartItems.value =
            currentItems.map { cartItem ->
                cartItem.copy(checked = existingCartItems.find { it.id == cartItem.id }?.checked ?: false)
            }
    }

    override fun selected(cartItemId: Long) {
        _cartItems.value =
            cartItems.value.orEmpty().map { cartItem ->
                cartItem.takeIf { it.id == cartItemId }
                    ?.run { copy(checked = !checked) }
                    ?: cartItem
            }

        updateTotalPrice()
        updateSelectedCartItemsCount()
        _isAllSelected.value = cartItems.value.orEmpty().all(CartItem::checked)
    }

    private fun updateTotalPrice() {
        _selectedCartItemsTotalPrice.value =
            cartItems.value.orEmpty()
                .filter(CartItem::checked)
                .sumOf(CartItem::price)
    }

    fun onBackClick() {
        _event.setValue(ShoppingCartEvent.PopBackStack)
    }

    override fun selectedAll() {
        if (isAllSelected.value == true) {
            updateCartItemsChecked(checked = false)
            updateTotalPrice()
            _isAllSelected.value = false
            return
        }
        updateCartItemsChecked(checked = true)
        updateTotalPrice()
        updateSelectedCartItemsCount()
        _isAllSelected.value = true
    }

    private fun updateCartItemsChecked(checked: Boolean) {
        _cartItems.value = cartItems.value?.map { cartItem -> cartItem.copy(checked = checked) }
    }

    companion object {
        private const val TAG = "ShoppingCartViewModel"

        private const val INCREASE_AMOUNT = 1

        fun factory(
            shoppingCartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    cartSource = ShoppingApp.cartSource,
                ),
            orderRepository: OrderRepository =
                DefaultOrderRepository(
                    orderSource = ShoppingApp.orderSource,
                    cartSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingCartRepository, orderRepository)
            }
    }
}
