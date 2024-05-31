package woowacourse.shopping.presentation.ui.cart.selection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState

class SelectionViewModel(
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(),
    SelectionEventHandler,
    SelectionCountHandler {
    private val pageSize = PAGE_SIZE

    private val _currentPage = MutableLiveData(DEFAULT_PAGE)
    val currentPage: LiveData<Int> = _currentPage

    private val _order = MutableLiveData<Order>(Order())
    val order: LiveData<Order>
        get() = _order

    private val _totalItemSize = MutableLiveData<Int>(cartRepository.size())
    val totalItemSize: LiveData<Int>
        get() = _totalItemSize

    private var lastPage: Int = DEFAULT_PAGE

    val cartItemsState: LiveData<UIState<List<CartItem>>> =
        currentPage.switchMap { page ->
            MutableLiveData<UIState<List<CartItem>>>().apply {
                value =
                    try {
                        setUpUIState(page)
                    } catch (e: Exception) {
                        UIState.Error(e)
                        setUpUIState(page)
                    }
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

    private val _totalPrice = MutableLiveData<Long>(order.value?.getTotalPrice() ?: 0L)
    val totalPrice: LiveData<Long>
        get() = _totalPrice

    private val _totalQuantity = MutableLiveData<Int>(order.value?.getTotalQuantity() ?: 0)
    val totalQuantity: LiveData<Int>
        get() = _totalQuantity

    private fun setUpUIState(page: @JvmSuppressWildcards Int): UIState<List<CartItem>> {
        val items = cartRepository.findAll().items
        return if (items.isEmpty()) {
            UIState.Empty
        } else {
            UIState.Success(items)
        }
    }

    init {
        loadPage()
    }

    private fun updatePageControlVisibility() {
        _totalItemSize.postValue(cartRepository.size())
        lastPage = ((totalItemSize.value ?: 0) - PAGE_STEP) / pageSize
    }

    private fun loadPage() {
        _currentPage.value = currentPage.value?.coerceIn(DEFAULT_PAGE, lastPage)
        updatePageControlVisibility()
    }

    fun deleteItem(itemId: Long) {
        cartRepository.delete(itemId)
        loadPage()
    }

    fun isCartEmpty() {
        _isEmpty.postValue(true)
    }

    override fun onCheckItem(itemId: Long) {
        val cartItem = cartRepository.findWithCartItemId(itemId)
        if (cartItem.isChecked) {
            removeFromOrder(cartItem)
        } else {
            addToOrder(cartItem)
        }
        // To notify the change of order
        _order.value = _order.value
        updatePriceAndQuantity()
    }

    private fun updatePriceAndQuantity() {
        _totalPrice.value = _order.value?.getTotalPrice() ?: 0L
        _totalQuantity.value = _order.value?.getTotalQuantity() ?: 0
    }

    private fun removeFromOrder(cartItem: CartItem) {
        _order.value?.removeCartItem(cartItem)
    }

    private fun addToOrder(cartItem: CartItem) {
        _order.value?.addCartItem(cartItem)
    }

    override fun navigateToDetail(productId: Long) {
        _navigateToDetail.postValue(Event(productId))
    }

    override fun deleteCartItem(itemId: Long) {
        _deleteCartItem.postValue(Event(itemId))
    }

    override fun increaseCount(productId: Long) {
        val currentQuantity = cartRepository.findQuantityWithProductId(productId)
        cartRepository.updateQuantityWithProductId(productId, currentQuantity + 1)
        loadPage()
    }

    override fun decreaseCount(productId: Long) {
        val currentQuantity = cartRepository.findQuantityWithProductId(productId)
        if (currentQuantity > 1) {
            cartRepository.updateQuantityWithProductId(productId, currentQuantity - 1)
        }
        loadPage()
    }

    fun selectAllByCondition() {
        if (_order.value?.list?.size == cartRepository.size()) {
            unSelectAll()
        } else {
            selectAll()
        }
    }

    private fun unSelectAll() {
        _order.value?.clearOrder()
        _order.value = _order.value
        updatePriceAndQuantity()
    }

    private fun selectAll() {
        _order.value?.selectAllCartItems(cartRepository.findAll().items)
        _order.value = _order.value
        updatePriceAndQuantity()
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
    }
}
