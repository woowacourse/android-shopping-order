package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.ui.cart.listener.OnAllCartItemSelectedListener
import woowacourse.shopping.ui.cart.listener.OnCartItemDeleteListener
import woowacourse.shopping.ui.cart.listener.OnCartItemSelectedListener
import woowacourse.shopping.ui.cart.listener.OnNavigationOrderListener
import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.model.OrderInformation
import kotlin.concurrent.thread

class ShoppingCartViewModel(
    private val cartItemRepository: CartItemRepository,
) : ViewModel(),
    OnCartItemDeleteListener,
    OnItemQuantityChangeListener,
    OnCartItemSelectedListener,
    OnAllCartItemSelectedListener,
    OnNavigationOrderListener {
    private val uiHandler = Handler(Looper.getMainLooper())

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

    private var _navigationOrderEvent = MutableSingleLiveData<OrderInformation>()
    val navigationOrderEvent: SingleLiveData<OrderInformation> get() = _navigationOrderEvent

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadAll() {
        thread {
            val currentItems: List<CartItem> = cartItemRepository.loadCartItems()

            uiHandler.post {
                _cartItems.value = currentItems
                _isLoading.value = false
            }
        }
    }

    fun deleteItem(cartItemId: Long) {
        thread {
            cartItemRepository.removeCartItem(cartItemId)
            val currentItems: List<CartItem> = cartItemRepository.loadCartItems()

            uiHandler.post {
                _cartItems.value = currentItems
            }
        }
        updateSelectedCartItemsCount()
    }

    private fun updateSelectedCartItemsCount() {
        _selectedCartItemsCount.value = cartItems.value?.count { it.checked }
    }

    override fun navigateToOrder() {
        if (selectedCartItemsCount.value == 0) return

        val productIds = cartItems.value?.filter { it.checked }?.map { it.id } ?: return
        val orderAmount = selectedCartItemsTotalPrice.value ?: return
        val ordersCount = selectedCartItemsCount.value ?: return
        _navigationOrderEvent.setValue(
            OrderInformation(
                productIds,
                orderAmount,
                ordersCount,
            ),
        )
    }

    override fun delete(cartItemId: Long) {
        _deletedItemId.setValue(cartItemId)
    }

    override fun onIncrease(
        cartItemId: Long,
        quantity: Int,
    ) {
        thread {
            cartItemRepository.increaseCartItem(cartItemId, quantity)
            val currentItems = cartItemRepository.loadCartItems()
            uiHandler.post {
                updateCartItems(currentItems)
                updateTotalPrice()
            }
        }
    }

    override fun onDecrease(
        cartItemId: Long,
        quantity: Int,
    ) {
        thread {
            cartItemRepository.decreaseCartProduct(cartItemId, quantity)
            val currentItems = cartItemRepository.loadCartItems()
            uiHandler.post {
                updateCartItems(currentItems)
                updateTotalPrice()
                updateSelectedCartItemsCount()
            }
        }
    }

    private fun updateCartItems(currentItems: List<CartItem>) {
        val cartItems = cartItems.value ?: return
        _cartItems.value =
            currentItems.map { cartItem ->
                cartItem.copy(checked = cartItems.first { it.id == cartItem.id }.checked)
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

        fun factory(
            cartItemRepository: CartItemRepository =
                DefaultCartItemRepository(
                    cartItemDataSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(cartItemRepository)
            }
    }
}
