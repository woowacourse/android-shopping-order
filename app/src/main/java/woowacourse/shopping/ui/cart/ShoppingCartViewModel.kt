package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.MutableSingleLiveData
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.SingleLiveData
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.domain.repository.DefaultShoppingProductRepository
import woowacourse.shopping.domain.repository.ShoppingProductsRepository
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import woowacourse.shopping.ui.model.CartItem
import kotlin.concurrent.thread

class ShoppingCartViewModel(
    private val shoppingProductsRepository: ShoppingProductsRepository,
) : ViewModel(),
    OnProductItemClickListener,
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

    private var _navigationOrderEvent = MutableLiveData<List<Long>>(emptyList())
    val navigationOrderEvent: LiveData<List<Long>> get() = _navigationOrderEvent

    fun loadAll() {
        thread {
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()

            uiHandler.post {
                _cartItems.value = currentItems
            }
        }
    }

    fun deleteItem(cartItemId: Long) {
        thread {
            shoppingProductsRepository.removeShoppingCartProduct(cartItemId)
            val currentItems =
                shoppingProductsRepository.loadPagedCartItem()

            uiHandler.post {
                _cartItems.value = currentItems
            }
        }
        updateSelectedCartItemsCount()
    }

    fun updateSelectedCartItemsCount() {
        _selectedCartItemsCount.value = cartItems.value?.count { it.checked }
    }

    override fun navigateToOrder() {
        if (selectedCartItemsCount.value == 0) return

        _navigationOrderEvent.value = cartItems.value?.filter {
            it.checked
        }?.map { it.id }
    }

    override fun onClick(productId: Long) {
        _deletedItemId.setValue(productId)
    }

    override fun onIncrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            shoppingProductsRepository.increaseShoppingCartProduct(productId, quantity)
            val currentItems = shoppingProductsRepository.loadPagedCartItem()
            uiHandler.post {
                updateCartItems(currentItems)
                updateTotalPrice()
            }
        }
    }

    override fun onDecrease(
        productId: Long,
        quantity: Int,
    ) {
        thread {
            shoppingProductsRepository.decreaseShoppingCartProduct(productId, quantity)
            val currentItems = shoppingProductsRepository.loadPagedCartItem()
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
            shoppingProductsRepository: ShoppingProductsRepository =
                DefaultShoppingProductRepository(
                    productsSource = ShoppingApp.productSource,
                    cartSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(shoppingProductsRepository)
            }
    }
}
