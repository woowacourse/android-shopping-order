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
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.DefaultShoppingCartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.ui.cart.event.ShoppingCartEvent
import woowacourse.shopping.ui.cart.listener.ShoppingCartListener
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
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
            shoppingCartRepository.loadAllCartItems2()
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
            shoppingCartRepository.removeShoppingCartProduct2(cartItemId)
                .onSuccess {
                    val currentItems = shoppingCartRepository.loadAllCartItems()
                    _cartItems.postValue(currentItems)
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
            // TOOD: show toast message: "선택된 상품이 없습니다."
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            cartItems.value?.forEach { cartItem ->
                if (cartItem.checked) {
                    orderRepository.saveOrderItem2(cartItem.id, cartItem.quantity)
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
        }
    }

    override fun onRemove(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    // 여기서의 파라미터 productId 는 실제로는 cartItemId.
    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = cartItems.value?.find { it.id == productId }
                ?: throw NoSuchElementException("There is no product with id: $productId")
            // TODO: 위에서 못 찰을 수가 없음. 만약 못 찾는 다면, viewmodel 이 가진 error 객체에게 보내서 예기치 못한 오류 설정하기

            updateProductQuantity(productId, item, INCREASE_AMOUNT)
        }
    }

    private suspend fun updateProductQuantity(
        productId: Long,
        item: CartItem,
        changeAmount: Int
    ) {
        shoppingCartRepository.updateProductQuantity2(
            cartItemId = productId,
            quantity = item.quantity + changeAmount
        )
            .onSuccess {
                updateCartItems()
            }
            .onFailure {
                // TODO : handle error
                Log.d(TAG, "onIncrease: updateProductQuantity2: $it")
                throw it
            }
    }

    private suspend fun updateCartItems() {
        shoppingCartRepository.loadAllCartItems2()
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

    // 여기서의 파라미터 productId 는 사실 cartItemId 였나?
    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = cartItems.value?.find { it.id == productId }
                ?: throw NoSuchElementException("There is no product with id: $productId")
            // TODO: 위에서 못 찰을 수가 없음. 만약 못 찾는 다면, viewmodel 이 가진 error 객체에게 보내서 예기치 못한 오류 설정하기

            updateProductQuantity(productId, item, DECREASE_AMOUNT)
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
            orderRepository: OrderRepository =
                DefaultOrderRepository(
                    orderSource = ShoppingApp.orderSource,
                    productSource = ShoppingApp.productSource,
                    cartSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingCartRepository, orderRepository)
            }
    }
}
