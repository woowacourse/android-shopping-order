package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel(),
    CartEventHandler,
    CartItemCountHandler {
    private val pageSize = PAGE_SIZE

    private val _currentPage = MutableLiveData(DEFAULT_PAGE)
    val currentPage: LiveData<Int> = _currentPage

    val isFirstPage: LiveData<Boolean> =
        currentPage.map { page ->
            page == DEFAULT_PAGE
        }

    val isLastPage: LiveData<Boolean> =
        currentPage.map { page ->
            page == lastPage
        }
    private val _totalItemSize = MutableLiveData<Int>()

    val totalItemSize: LiveData<Int> = _totalItemSize
    private val _isPageControlVisible =
        MutableLiveData<Boolean>(((totalItemSize.value ?: 0) > PAGE_SIZE))

    val isPageControlVisible: LiveData<Boolean> = _isPageControlVisible
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
    private val _navigateToShopping = MutableLiveData<Event<Boolean>>()

    val navigateToShopping: LiveData<Event<Boolean>>
        get() = _navigateToShopping
    private val _navigateToDetail = MutableLiveData<Event<Long>>()

    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail
    private val _deleteCartItem = MutableLiveData<Event<Long>>()

    val deleteCartItem: LiveData<Event<Long>>
        get() = _deleteCartItem

    private val _totalPrice = MutableLiveData<Long>(0)
    val totalPrice: LiveData<Long>
        get() = _totalPrice

    private val _totalQuantity = MutableLiveData<Int>(0)
    val totalQuantity: LiveData<Int>
        get() = _totalQuantity

    init {
        loadPage()
    }

    private fun setUpUIState(page: @JvmSuppressWildcards Int): UIState<List<CartItem>> {
        var state: UIState<List<CartItem>> = UIState.Empty
        viewModelScope.launch {
            val items = cartRepository.findAllPagedItems(page, pageSize)
            items.onSuccess {
                state =
                    if (it.items.isEmpty()) {
                        UIState.Empty
                    } else {
                        UIState.Success(it.items)
                    }
            }
        }
        return state
    }

    private fun updatePageControlVisibility() {
        viewModelScope.launch {
            val size = cartRepository.size()
            size.onSuccess {
                _totalItemSize.postValue(it)
            }
        }
        lastPage = ((totalItemSize.value ?: 0) - PAGE_STEP) / pageSize
        _isPageControlVisible.postValue((totalItemSize.value ?: 0) > pageSize)
    }

    private fun loadPage() {
        _currentPage.value = currentPage.value?.coerceIn(DEFAULT_PAGE, lastPage)
        updatePageControlVisibility()
    }

    fun loadNextPage() {
        val nextPage = (currentPage.value ?: DEFAULT_PAGE) + PAGE_STEP
        _currentPage.value = nextPage.coerceIn(DEFAULT_PAGE, lastPage)
        updatePageControlVisibility()
    }

    fun loadPreviousPage() {
        val prevPage = (currentPage.value ?: DEFAULT_PAGE) - PAGE_STEP
        _currentPage.value = prevPage.coerceIn(DEFAULT_PAGE, lastPage)
        updatePageControlVisibility()
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            val deleteTransaction = cartRepository.delete(itemId)
            deleteTransaction.onSuccess {
                loadPage()
            }
        }
    }

    fun isCartEmpty() {
        _isEmpty.postValue(true)
    }

    override fun navigateToShopping() {
        _navigateToShopping.postValue(Event(true))
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

    /*override fun increaseCount(productId: Long) {
        val currentQuantity = cartRepository.findQuantityWithProductId(productId)
        cartRepository.updateQuantityWithProductId(productId, currentQuantity + 1)
        loadPage()
    }*/

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

    /*override fun decreaseCount(productId: Long) {
        val currentQuantity = cartRepository.findQuantityWithProductId(productId)
        if (currentQuantity > 1) {
            cartRepository.updateQuantityWithProductId(productId, currentQuantity - 1)
        }
        loadPage()
    }*/

    companion object {
        private const val PAGE_SIZE = 5
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
    }
}
