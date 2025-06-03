package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.model.FetchPageDirection
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toSuggestionUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.cart.event.CartMessageEvent
import kotlin.math.max

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<CartMessageEvent>()
    val toastEvent: SingleLiveData<CartMessageEvent> = _toastEvent

    private val cartItems = MutableLiveData<List<CartProductUiModel>>(emptyList())
    private val selectedCartItems = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val cartDisplayItems =
        MediatorLiveData<List<DisplayModel<CartProductUiModel>>>().apply {
            addSource(cartItems) { cartItems ->
                val currentSelectedCartItems = selectedCartItems.value.orEmpty()
                value = cartItems.map { DisplayModel(it, currentSelectedCartItems.contains(it)) }
            }
            addSource(selectedCartItems) { selectedCartItems ->
                val currentCartItems = cartItems.value.orEmpty()
                value = currentCartItems.map { DisplayModel(it, selectedCartItems.contains(it)) }
            }
        }

    private val _suggestionProducts =
        MutableLiveData<List<SuggestionProductUiModel>>(
            emptyList(),
        )
    val suggestionProducts: LiveData<List<SuggestionProductUiModel>> = _suggestionProducts

    val totalPrice: LiveData<Int> =
        selectedCartItems.map { it.sumOf { cartProduct -> cartProduct.totalPrice } }

    val totalCount: LiveData<Int> =
        selectedCartItems.map { selectedCartItems -> selectedCartItems.sumOf { it.quantity } }

    private val _isCheckAll = MutableLiveData<Boolean>()
    val isCheckAll: LiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSource(selectedCartItems) { selectedCartItems ->
                value = selectedCartItems.size == cartRepository.fetchAllCartItems().size
            }
            addSource(_isCheckAll) { isCheckAll ->
                value = isCheckAll
            }
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
                .onSuccess { onFetchCartItemsSuccess(it, newPage) }
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

    fun increaseProductQuantity(
        productId: Long,
        refreshTarget: RefreshTarget,
    ) {
        cartRepository.insertCartProductQuantityToCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshProductQuantity(refreshTarget) }
                .onFailure { postFailureEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun decreaseProductQuantity(
        productId: Long,
        refreshTarget: RefreshTarget,
    ) {
        cartRepository.decreaseCartProductQuantityFromCart(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshProductQuantity(refreshTarget) }
                .onFailure { postFailureEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun switchCartItemSelection(cartId: Long) {
        val selectedItems = selectedCartItems.value.orEmpty()
        if (selectedItems.find { it.cartId == cartId } != null) {
            selectedCartItems.value = selectedItems.filter { it.cartId != cartId }
            return
        }
        val foundCartProduct = this.cartItems.value?.find { it.cartId == cartId } ?: return
        selectedCartItems.value = selectedItems + foundCartProduct
    }

    fun fetchSuggestionProducts() {
        val excludedProductIds =
            this.cartItems.value
                .orEmpty()
                .filter { it.quantity != 0 }
                .map { it.productId }

        productRepository.fetchSuggestionProducts(SUGGESTION_LIMIT, excludedProductIds) { result ->
            result
                .onSuccess { combine(it) }
                .onFailure { postFailureEvent(CartMessageEvent.FETCH_SUGGESTION_PRODUCT_FAILURE) }
        }
    }

    fun selectCurrentPageCartProduct() {
        _isCheckAll.value = !(_isCheckAll.value ?: false)
        val checked = (_isCheckAll.value ?: false)
        if (checked) {
            selectedCartItems.value =
                cartRepository.fetchAllCartItems().map { it.toCartItemUiModel() }
            return
        }
        selectedCartItems.value = emptyList()
    }

    private fun combine(suggestionProducts: List<Product>) {
        val ids = suggestionProducts.map { it.id }
        cartRepository
            .findCartProductsByProductIds(ids)
            .onFailure { CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE }
            .onSuccess {
                val updatedItems = applyCartQuantities(it, suggestionProducts)
                _suggestionProducts.postValue(updatedItems)
            }
    }

    private fun applyCartQuantities(
        cartProducts: List<CartProduct>,
        suggestionProducts: List<Product>,
    ): List<SuggestionProductUiModel> {
        val cartItemMap = cartProducts.associateBy { it.product.id }
        return suggestionProducts.map { product ->
            val found = cartItemMap[product.id]
            if (found != null) return@map product.toSuggestionUiModel(found.quantity)
            product.toSuggestionUiModel(DEFAULT_QUANTITY)
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

    private fun refreshProductQuantity(refreshTarget: RefreshTarget) {
        when (refreshTarget) {
            RefreshTarget.CART -> refreshFetchItem()
            RefreshTarget.SUGGESTION -> fetchSuggestionProducts()
        }
    }

    private fun refreshFetchItem() {
        val newPage = calculatePage(FetchPageDirection.CURRENT)
        cartRepository.fetchCartItems(newPage, limit) { result ->
            result
                .onSuccess { onRefreshProductQuantitySuccess(it) }
                .onFailure { postFailureEvent(CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun handleFetchCartItemDeleted(deletedCartId: Long) {
        val selectedItems = selectedCartItems.value.orEmpty().toMutableList()
        selectedItems.removeIf { it.cartId == deletedCartId }
        selectedCartItems.postValue(selectedItems)

        val items = this.cartItems.value.orEmpty()
        val isLastItem = items.size == 1 && items.first().cartId == deletedCartId

        fetchCartItems(if (isLastItem) FetchPageDirection.PREVIOUS else FetchPageDirection.CURRENT)
    }

    private fun onFetchCartItemsSuccess(
        pageableItem: PageableItem<CartProduct>,
        newPage: Int,
    ) {
        this.cartItems.postValue(pageableItem.items.map { it.toCartItemUiModel() })
        _hasMore.postValue(pageableItem.hasMore)
        _page.postValue(newPage)
        _isLoading.postValue(false)
    }

    private fun onRefreshProductQuantitySuccess(pageableItem: PageableItem<CartProduct>) {
        val (cartItems, hasMore) = pageableItem
        val uiModels = cartItems.map { it.toCartItemUiModel() }
        this.cartItems.postValue(uiModels)
        _hasMore.postValue(hasMore)

        updateSelectedCartProducts(uiModels)
    }

    private fun updateSelectedCartProducts(uiModels: List<CartProductUiModel>) {
        val currentSelectedItems = selectedCartItems.value.orEmpty()
        val updatedSelectedItems =
            currentSelectedItems.map { item ->
                val foundItem = uiModels.find { it.productId == item.productId }
                if (foundItem == null) return@map item
                foundItem
            }
        selectedCartItems.postValue(updatedSelectedItems)
    }

    private fun postFailureEvent(event: CartMessageEvent) {
        _toastEvent.postValue(event)
    }

    private fun CartProduct.isSelected(): Boolean {
        val selectedItems = selectedCartItems.value
        return selectedItems?.any { it.cartId == cartId } ?: false
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
        private const val QUANTITY_STEP = 1
        private const val DEFAULT_QUANTITY = 0
        private const val SUGGESTION_LIMIT = 10

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val productRepository = RepositoryProvider.productRepository
                    return OrderViewModel(cartRepository, productRepository) as T
                }
            }
    }
}
