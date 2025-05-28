package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _products = MutableLiveData<List<CartItem>>()
    val products: LiveData<List<CartItem>> = _products

    private val _deleteState = MutableLiveData<Long>()
    val deleteState: LiveData<Long> = _deleteState

    private val _page = MutableLiveData(START_PAGE)
    val page: LiveData<Int> = _page

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _itemUpdateEvent = MutableLiveData<CartItem>()
    val itemUpdateEvent: LiveData<CartItem> = _itemUpdateEvent

    private val limit: Int = 5

    init {
        fetchShoppingCart(false)
    }

    fun fetchShoppingCart(
        isNextPage: Boolean,
        isRefresh: Boolean = false,
    ) {
        val currentPage = _page.value ?: DEFAULT_PAGE
        val newPage = calculatePage(isNextPage, currentPage, isRefresh)

        cartRepository.getCartItems(
            page = newPage - DEFAULT_PAGE,
            limit = limit,
        ) { products, hasMore ->
            _products.postValue(products.map { it })
            _page.postValue(newPage)
            _hasMore.postValue(hasMore)
        }
    }

    fun deleteProduct(cartItem: CartItem) {
        cartRepository.deleteCartItem(cartItem.cartId) {
            _deleteState.postValue(it)
        }
    }

    fun increaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        cartRepository.increaseCartItem(cartItem) { id ->
            getCartItemById(id) {
                it?.let { item -> _itemUpdateEvent.postValue(item) }
            }
        }
    }

    fun decreaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()

        if (cartItem.amount <= 1) {
            deleteProduct(cartItem)
        } else {
            cartRepository.decreaseCartItem(cartItem) { id ->
                getCartItemById(id) {
                    it?.let { item -> _itemUpdateEvent.postValue(item) }
                }
            }
        }
    }

    private fun calculatePage(
        isNextPage: Boolean,
        currentPage: Int,
        isRefresh: Boolean,
    ): Int {
        if (isRefresh) return currentPage

        return if (isNextPage) {
            currentPage + DEFAULT_PAGE
        } else {
            max(DEFAULT_PAGE, currentPage - DEFAULT_PAGE)
        }
    }

    private fun getCartItemById(
        id: Long,
        callback: (CartItem?) -> Unit,
    ) {
        cartRepository.getAllCartItems { items ->
            val foundItem = items?.find { it.cartId == id }
            callback(foundItem)
        }
    }

    companion object {
        private const val START_PAGE = 0
        private const val DEFAULT_PAGE = 1
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val repository = RepositoryProvider.cartRepository
                    return CartViewModel(repository) as T
                }
            }
    }
}
