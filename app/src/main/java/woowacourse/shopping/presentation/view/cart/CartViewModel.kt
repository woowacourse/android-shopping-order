package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.FetchPageDirection
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.cart.event.CartMessageEvent
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<CartMessageEvent>()
    val toastEvent: SingleLiveData<CartMessageEvent> = _toastEvent

    private val _cartItems = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val cartItems: LiveData<List<CartProductUiModel>> = _cartItems

    private val _selectedCartItems = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val selectedCartItems: LiveData<List<CartProductUiModel>> = _selectedCartItems

    val totalPrice: LiveData<Int> =
        _selectedCartItems.map { it.sumOf { cartProduct -> cartProduct.totalPrice } }

    val totalCount: LiveData<Int> = _selectedCartItems.map { it.count() }

    val isCheckAll: LiveData<Boolean> =
        _selectedCartItems.map { selectedCartItems ->
            cartItems.value?.all { selectedCartItems.contains(it) } ?: false
        }

    private val _page = MutableLiveData(DEFAULT_PAGE)
    val page: LiveData<Int> = _page.map { it + 1 }

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val limit = 5

    init {
        fetchCartItems(FetchPageDirection.CURRENT)
    }

    fun fetchCartItems(direction: FetchPageDirection) {
        val newPage = calculatePage(direction)
        _isLoading.postValue(true)

        cartRepository.fetchCartItems(newPage, limit) { result ->
            result
                .onSuccess { handleFetchCartItemsSuccess(it, newPage) }
                .onFailure { postFailureEvent(CartMessageEvent.FETCH_CART_ITEMS_FAILURE) }
        }
    }

    fun deleteCartItem(cartId: Long) {
        cartRepository.deleteCartItem(cartId) { result ->
            result
                .onSuccess { handleFetchCartItemDeleted(cartId) }
                .onFailure { postFailureEvent(CartMessageEvent.DELETE_CART_ITEM_FAILURE) }
        }
    }

    fun increaseProductQuantity(productId: Long) {
        cartRepository.insertCartProductQuantityToCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshProductQuantity() }
                .onFailure { postFailureEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun decreaseProductQuantity(productId: Long) {
        cartRepository.decreaseCartProductQuantityFromCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshProductQuantity() }
                .onFailure { postFailureEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun calculatePage(direction: FetchPageDirection): Int {
        val currentPage = _page.value ?: DEFAULT_PAGE
        return when (direction) {
            FetchPageDirection.PREVIOUS -> max(DEFAULT_PAGE, currentPage - PAGE_STEP)
            FetchPageDirection.CURRENT -> currentPage
            FetchPageDirection.NEXT -> currentPage + PAGE_STEP
        }
    }

    private fun handleFetchCartItemsSuccess(
        pageableItem: PageableItem<CartProduct>,
        newPage: Int,
    ) {
        _cartItems.postValue(pageableItem.items.map { it.toCartItemUiModel() })
        _hasMore.postValue(pageableItem.hasMore)
        _page.postValue(newPage)
        _isLoading.postValue(false)
    }

    private fun refreshProductQuantity() {
        val newPage = calculatePage(FetchPageDirection.CURRENT)
        cartRepository.fetchCartItems(newPage, limit) { result ->
            result
                .onSuccess { pageableItem ->
                    val (cartItems, hasMore) = pageableItem
                    val uiModels = cartItems.map { it.toCartItemUiModel() }
                    _cartItems.postValue(uiModels)
                    _hasMore.postValue(hasMore)
                }.onFailure {
                    postFailureEvent(CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE)
                }
        }
    }

    private fun handleFetchCartItemDeleted(deletedCartId: Long) {
        val items = _cartItems.value.orEmpty()
        val isLastItem = items.size == 1 && items.first().cartId == deletedCartId

        fetchCartItems(if (isLastItem) FetchPageDirection.PREVIOUS else FetchPageDirection.CURRENT)
    }

    private fun postFailureEvent(event: CartMessageEvent) {
        _toastEvent.postValue(event)
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
        private const val QUANTITY_STEP = 1

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T = CartViewModel(RepositoryProvider.cartRepository) as T
            }
    }
}
