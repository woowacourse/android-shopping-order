package woowacourse.shopping.presentation.ui.cart.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class SelectionViewModel(
    private val cartRepository: CartRepository,
) : ViewModel(),
    SelectionEventHandler,
    CounterHandler {
    private val pageSize = PAGE_SIZE

    private val _currentPage = MutableLiveData(DEFAULT_PAGE)
    val currentPage: LiveData<Int> = _currentPage

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _order = MutableLiveData<Order>(OrderDatabase.getOrder())
    val order: LiveData<Order>
        get() = _order

    private val _totalItemSize = MutableLiveData<Int>()
    val totalItemSize: LiveData<Int>
        get() = _totalItemSize

    private var lastPage: Int = DEFAULT_PAGE

    val cartItemsState: LiveData<UIState<List<CartItem>>> =
        currentPage.switchMap { page ->
            MutableLiveData<UIState<List<CartItem>>>().apply {
                viewModelScope.launch {
                    value =
                        try {
                            setUpUIState(page)
                        } catch (e: Exception) {
                            UIState.Error(e)
                            setUpUIState(page)
                        }
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

    init {
        loadPage()
    }

    private suspend fun setUpUIState(page: @JvmSuppressWildcards Int): UIState<List<CartItem>> {
        var state: UIState<List<CartItem>> = UIState.Empty
        val transaction =
            viewModelScope.async {
                cartRepository.findAll()
            }
        transaction.await().onSuccess {
            if (it.items.isEmpty()) {
                state = UIState.Empty
            } else {
                state = UIState.Success(it.items)
            }
        }
        Log.d("crong", "setUpUIState: $state")
        return state
    }

    private fun loadPage() {
        _currentPage.value = currentPage.value?.coerceIn(DEFAULT_PAGE, lastPage)
        updatePageControlVisibility()
    }

    private fun updatePageControlVisibility() {
        viewModelScope.launch {
            val size = cartRepository.size()
            size.onSuccess {
                _totalItemSize.postValue(it)
                lastPage = ((totalItemSize.value ?: 0) - PAGE_STEP) / pageSize
            }
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            cartRepository.delete(itemId)
        }
        loadPage()
    }

    fun isCartEmpty() {
        _isEmpty.postValue(true)
    }

    fun onLoading() {
        _loading.postValue(true)
    }

    fun onLoaded() {
        _loading.postValue(false)
    }

    override fun onCheckItem(itemId: Long) {
        viewModelScope.launch {
            val cartItem = cartRepository.findWithCartItemId(itemId)
            cartItem.onSuccess {
                if (it.isChecked) {
                    removeFromOrder(it)
                } else {
                    addToOrder(it)
                }
            }
            // To notify the change of order
            _order.value = _order.value
        }
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
        viewModelScope.launch {
            val currentQuantity = cartRepository.findQuantityWithProductId(productId)
            currentQuantity.onSuccess {
                cartRepository.updateQuantityWithProductId(productId, it + 1)
            }
        }
        loadPage()
    }

    override fun decreaseCount(productId: Long) {
        viewModelScope.launch {
            val currentQuantity = cartRepository.findQuantityWithProductId(productId)
            currentQuantity.onSuccess {
                if (it > 1) {
                    cartRepository.updateQuantityWithProductId(productId, it - 1)
                }
            }
        }
        loadPage()
    }

    fun selectAllByCondition() {
        viewModelScope.launch {
            val size = cartRepository.size()
            size.onSuccess {
                if (_order.value?.list?.size == it) {
                    unSelectAll()
                } else {
                    selectAll()
                }
            }
        }
    }

    private fun unSelectAll() {
        _order.value?.clearOrder()
        _order.value = _order.value
        updatePriceAndQuantity()
    }

    private fun selectAll() {
        viewModelScope.launch {
            val items = cartRepository.findAll()
            items.onSuccess {
                _order.value?.selectAllCartItems(it.items)
                _order.value = _order.value
            }
        }
        updatePriceAndQuantity()
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
    }
}
