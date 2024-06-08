package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import kotlin.concurrent.thread

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(),
    ShoppingCartListener {
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

    private val _event: MutableSingleLiveData<ShoppingCartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ShoppingCartEvent> get() = _event

    fun loadAll() {
        thread {
            val currentItems = shoppingCartRepository.loadAllCartItems()
            _cartItems.postValue(currentItems)
        }
    }

    fun deleteItem(cartItemId: Long) {
        thread {
            shoppingCartRepository.removeShoppingCartProduct(cartItemId)
        }.join()

        thread {
            val currentItems = shoppingCartRepository.loadAllCartItems()
            _cartItems.postValue(currentItems)
        }.join()

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
        if (selectedCartItemsCount.value == 0) return

        thread {
            _event.postValue(ShoppingCartEvent.NavigationOrder)

            cartItems.value?.forEach { cartItem ->
                if (cartItem.checked) {
                    orderRepository.saveOrderItem(cartItem.product.id, cartItem.quantity)
                }
            }
        }
    }

    override fun onRemove(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    // 여기서의 파라미터 productId 는 사실 cartItemId 였나?
    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            val item =
                cartItems.value?.find { it.id == productId }
                    ?: throw NoSuchElementException("There is no product with id: $productId")
            shoppingCartRepository.updateProductQuantity(cartItemId = productId, quantity = item.quantity + 1)
            val currentItems = shoppingCartRepository.loadAllCartItems()

            uiHandler.post {
                updateCartItems(currentItems)

                updateTotalPrice()
                updateSelectedCartItemsCount()
            }
        }
    }

    // 여기서의 파라미터 productId 는 사실 cartItemId 였나?
    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            val item =
                cartItems.value?.find { it.id == productId }
                    ?: throw NoSuchElementException("There is no product with id: $productId")
            shoppingCartRepository.updateProductQuantity(cartItemId = productId, quantity = item.quantity - 1)

            val currentItems = shoppingCartRepository.loadAllCartItems()

            uiHandler.post {
                updateCartItems(currentItems)
                updateTotalPrice()
                updateSelectedCartItemsCount()
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

        fun factory(
            shoppingCartRepository: ShoppingCartRepository =
                DefaultShoppingCartRepository(
                    cartSource = ShoppingApp.cartSource,
                ),
            orderRepository: OrderRepository =
                DefaultOrderRepository(
                    orderSource = ShoppingApp.orderSource,
                    productSource = ShoppingApp.productSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingCartRepository, orderRepository)
            }
    }
}
