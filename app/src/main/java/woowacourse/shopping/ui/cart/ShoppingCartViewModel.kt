package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.repository.DefaultOrderRepository2
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository2
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.ui.cart.event.ShoppingCartEvent
import woowacourse.shopping.ui.cart.listener.ShoppingCartListener
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ShoppingCartViewModel(
    private val cartRepository: ShoppingCartRepository,
    private val realOrderRepository2: OrderRepository2
) : ViewModel(),
    ShoppingCartListener {

    private var _cartItems = MutableLiveData<List<CartItem>>()
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

    fun loadAll() {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.loadAllCartItems()
                .onSuccess {
                    _cartItems.postValue(it)
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "loadAll: failure: $it")
                    throw it
                }
        }
    }

    fun deleteItem(cartItemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.removeShoppingCartProduct(cartItemId)
                .onSuccess {
                    cartRepository.loadAllCartItems()
                        .onSuccess {
                            _cartItems.postValue(it)
                        }
                        .onFailure {
                            // TODO : handle error
                            Log.e(TAG, "deleteItem: loadAllCartItems2: failure: $it")
                            throw it
                        }
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "deleteItem: failure: $it")
                    throw it
                }
        }

        updateTotalPrice()
        updateSelectedCartItemsCount()
    }

    private fun updateSelectedCartItemsCount() {
        _selectedCartItemsCount.value =
            cartItems.value?.sumOf { cartItem ->
                if (cartItem.checked) {
                    cartItem.quantity
                } else {
                    0
                }
            }
    }

    override fun navigateToOrder() {
        if (selectedCartItemsCount.value == 0) {
            // TODO: show toast message: "선택된 상품이 없습니다."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            cartItems.value?.forEach { cartItem ->
                if (cartItem.checked) {
                    realOrderRepository2.save(
                        orderItem = OrderItem(
                            cartItemId = cartItem.id,
                            quantity = cartItem.quantity,
                            product = cartItem.product
                        )
                    )
                        .onSuccess {
                            _event.postValue(ShoppingCartEvent.NavigationOrder)
                        }
                        .onFailure {
                            // TODO : handle error
                            Log.d(TAG, "navigateToOrder: failure: $it")
                            throw it
                        }
                }
            }

            Log.d(
                TAG, "navigateToOrder: orderItems: " +
                        "${realOrderRepository2.loadAllOrders().getOrThrow()}"
            )
        }
    }

    override fun onRemove(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.addShoppingCartProduct(productId, INCREASE_AMOUNT)
                .onSuccess {
                    updateCartItems()
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "onIncrease: addShoppingCartProduct2: $it")
                    throw it
                }
        }
    }

    private suspend fun updateCartItems() {
        cartRepository.loadAllCartItems()
            .onSuccess { cartItems ->
                withContext(Dispatchers.Main) {
                    updateCartItems(cartItems)
                    updateTotalPrice()
                    updateSelectedCartItemsCount()
                }
            }
            .onFailure {
                // TODO: handle error
                Log.d(TAG, "onIncrease - loadAllCartItems2 : failure: $it")
                throw it
            }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        val cart = cartItems.value?.find { cartItem ->
            cartItem.product.id == productId
        } ?: return

        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.updateProductQuantity(cart.id, quantity)
                .onSuccess {
                    updateCartItems()
                }
                .onFailure {
                    // TODO : handle error
                    Log.d(TAG, "onIncrease: updateProductQuantity2: $it")
                    throw it
                }
        }
    }

    private fun updateCartItems(currentItems: List<CartItem>) {
        _cartItems.value =
            currentItems.map { cartItem ->
                // TODO: 널 단언 제거하기
                cartItem.copy(checked = cartItems.value?.find { it.id == cartItem.id }!!.checked)
            }
    }

    override fun selected(cartItemId: Long) {
        val selectedItem =
            cartItems.value?.find { it.id == cartItemId } ?: throw IllegalStateException()
        val changedItem = selectedItem.copy(checked = !selectedItem.checked)

        _cartItems.value =
            cartItems.value?.map {
                if (it.id == cartItemId) {
                    changedItem
                } else {
                    it
                }
            }
        updateTotalPrice()
        updateSelectedCartItemsCount()
        _isAllSelected.value = cartItems.value?.all { it.checked }
    }

    private fun updateTotalPrice() {
        _selectedCartItemsTotalPrice.value =
            cartItems.value?.filter { it.checked }?.sumOf {
                it.product.price * it.quantity
            }
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
        _cartItems.value =
            cartItems.value?.map { cartItem ->
                cartItem.copy(checked = checked)
            }
    }

    companion object {
        private const val TAG = "ShoppingCartViewModel"

        private const val INCREASE_AMOUNT = 1
        private const val DECREASE_AMOUNT = -1

        fun factory(
            shoppingCartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    cartSource = ShoppingApp.cartSource,
                ),
            realOrderRepository2: OrderRepository2 =
                DefaultOrderRepository2(
                    orderSource = ShoppingApp.orderSource2,
                    cartSource = ShoppingApp.cartSource,
                )
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingCartRepository, realOrderRepository2)
            }
    }
}
