package woowacourse.shopping.presentation.view.order

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
import woowacourse.shopping.presentation.view.order.cart.event.CartMessageEvent
import kotlin.math.max

class OrderViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<CartMessageEvent>()
    val toastEvent: SingleLiveData<CartMessageEvent> = _toastEvent

    private val _cartProducts = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val cartProducts: LiveData<List<CartProductUiModel>> = _cartProducts

    private val _page = MutableLiveData(DEFAULT_PAGE)
    val page: LiveData<Int> = _page.map { page -> page + 1 }

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val orderProducts = MutableLiveData<List<CartProductUiModel>>(emptyList())

    val orderTotalPrice: LiveData<Int> =
        orderProducts.map { products ->
            products.sumOf { product -> product.totalPrice }
        }

    val orderTotalCount: LiveData<Int> =
        orderProducts.map { products ->
            products.sumOf { product -> product.quantity }
        }

    init {
        fetchCartItems(FetchPageDirection.CURRENT)
    }

    fun fetchCartItems(direction: FetchPageDirection) {
        val targetPage = calculateTargetPage(direction)
        startLoading()

        cartRepository.fetchCartProducts(targetPage, ITEMS_PER_PAGE) { result ->
            result
                .onSuccess { pageableItem -> handleCartItemsFetchSuccess(pageableItem, targetPage) }
                .onFailure { postToastEvent(CartMessageEvent.FETCH_CART_ITEMS_FAILURE) }
        }
    }

    fun increaseProductQuantity(productId: Long) {
        cartRepository.increaseQuantity(productId, QUANTITY_STEP) { result ->
            result
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
                .onSuccess {
                    refreshCurrentPageItems()
                    addOrderProductFromSuggestion(productId)
                }
        }
    }

    fun decreaseProductQuantity(productId: Long) {
        cartRepository.decreaseQuantity(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshCurrentPageItems() }
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    fun deleteCartItem(cartId: Long) {
        cartRepository.deleteCartProduct(cartId) { result ->
            result
                .onSuccess { handleCartItemDeletionSuccess(cartId) }
                .onFailure { postToastEvent(CartMessageEvent.DELETE_CART_ITEM_FAILURE) }
        }
    }

    fun toggleOrderProductSelection(productId: Long) {
        val currentProducts = getCurrentOrderProducts()
        val updatedProducts = toggleProductInOrderList(currentProducts, productId)
        orderProducts.postValue(updatedProducts)
    }

    private fun getCurrentPage(): Int = _page.value ?: DEFAULT_PAGE

    private fun getCurrentOrderProducts() = orderProducts.value.orEmpty()

    private fun getCurrentCartItems(): List<CartProductUiModel> = _cartProducts.value.orEmpty()

    private fun startLoading() {
        _isLoading.postValue(true)
    }

    private fun stopLoading() {
        _isLoading.postValue(false)
    }

    private fun calculateTargetPage(direction: FetchPageDirection): Int {
        val currentPage = getCurrentPage()
        return when (direction) {
            FetchPageDirection.PREVIOUS -> max(DEFAULT_PAGE, currentPage - PAGE_STEP)
            FetchPageDirection.CURRENT -> currentPage
            FetchPageDirection.NEXT -> currentPage + PAGE_STEP
        }
    }

    private fun handleCartItemsFetchSuccess(
        pageableItem: PageableItem<CartProduct>,
        newPage: Int,
    ) {
        val uiModels = convertToUiModels(pageableItem.items)
        val itemsWithSelectionState = applySelectionState(uiModels)

        updateCartItemsState(itemsWithSelectionState, pageableItem.hasMore, newPage)
        stopLoading()
    }

    private fun handleCartItemDeletionSuccess(deletedCartId: Long) {
        removeProductFromOrderList(deletedCartId)
        val fetchDirection = determineFetchDirectionAfterDeletion(deletedCartId)
        fetchCartItems(fetchDirection)
    }

    private fun refreshCurrentPageItems() {
        val currentPage = getCurrentPage()
        cartRepository.fetchCartProducts(currentPage, ITEMS_PER_PAGE) { result ->
            result
                .onSuccess { pageableItem -> handleQuantityRefreshSuccess(pageableItem) }
                .onFailure { postToastEvent(CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun handleQuantityRefreshSuccess(pageableItem: PageableItem<CartProduct>) {
        val uiModels = convertToUiModels(pageableItem.items)
        val itemsWithSelectionState = applySelectionState(uiModels)

        val removedProductIds = findRemovedProductIds(itemsWithSelectionState)

        updateCartItemsAfterRefresh(itemsWithSelectionState, pageableItem.hasMore)
        removeDeletedProductsFromOrder(removedProductIds)
    }

    private fun addOrderProductFromSuggestion(productId: Long) {
        val existingProduct = getCurrentOrderProducts().any { it.productId == productId }
        if (existingProduct) return
        toggleOrderProductSelection(productId)
    }

    private fun toggleProductInOrderList(
        currentProducts: List<CartProductUiModel>,
        productId: Long,
    ): List<CartProductUiModel> {
        val mutableProducts = currentProducts.toMutableList()
        val wasRemoved = mutableProducts.removeIf { it.productId == productId }

        if (!wasRemoved) {
            val cartProduct = cartRepository.findCartProductByProductId(productId).getOrNull()
            cartProduct?.let { mutableProducts.add(it.toCartItemUiModel()) }
        }

        return mutableProducts
    }

    private fun removeProductFromOrderList(cartId: Long) {
        val currentProducts = getCurrentOrderProducts().toMutableList()
        currentProducts.removeIf { it.cartId == cartId }
        orderProducts.postValue(currentProducts)
    }

    private fun removeDeletedProductsFromOrder(deletedProductIds: Set<Long>) {
        if (deletedProductIds.isEmpty()) return

        val currentProducts = getCurrentOrderProducts()
        val filteredProducts = currentProducts.filter { it.productId !in deletedProductIds }

        if (filteredProducts.size != currentProducts.size) {
            orderProducts.postValue(filteredProducts)
        }
    }

    private fun updateOrderProductsWithLatestData(cartProducts: List<CartProductUiModel>) {
        val currentProducts = getCurrentOrderProducts()
        val updatedProducts =
            currentProducts.map { orderProduct ->
                cartProducts.find { it.productId == orderProduct.productId } ?: orderProduct
            }
        orderProducts.postValue(updatedProducts)
    }

    private fun applySelectionState(cartProducts: List<CartProductUiModel>): List<CartProductUiModel> {
        updateOrderProductsWithLatestData(cartProducts)
        val selectedProductIds = getSelectedProductIds()

        return cartProducts.map { cartProduct ->
            val isSelected = selectedProductIds.contains(cartProduct.productId)
            cartProduct.copy(isSelected = isSelected)
        }
    }

    private fun getSelectedProductIds(): Set<Long> = getCurrentOrderProducts().map { it.productId }.toSet()

    private fun convertToUiModels(cartProducts: List<CartProduct>): List<CartProductUiModel> = cartProducts.map { it.toCartItemUiModel() }

    private fun updateCartItemsState(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
        page: Int,
    ) {
        _cartProducts.postValue(items)
        _hasMore.postValue(hasMore)
        _page.postValue(page)
    }

    private fun updateCartItemsAfterRefresh(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
    ) {
        _cartProducts.postValue(items)
        _hasMore.postValue(hasMore)
    }

    private fun findRemovedProductIds(newItems: List<CartProductUiModel>): Set<Long> {
        val currentItems = getCurrentCartItems()
        val newProductIds = newItems.map { it.productId }.toSet()

        return currentItems
            .map { it.productId }
            .filter { it !in newProductIds }
            .toSet()
    }

    private fun determineFetchDirectionAfterDeletion(deletedCartId: Long): FetchPageDirection {
        val currentItems = getCurrentCartItems()
        val isLastItemOnPage =
            currentItems.size == 1 && currentItems.first().cartId == deletedCartId

        return if (isLastItemOnPage) FetchPageDirection.PREVIOUS else FetchPageDirection.CURRENT
    }

    private fun postToastEvent(event: CartMessageEvent) {
        _toastEvent.postValue(event)
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val PAGE_STEP = 1
        private const val QUANTITY_STEP = 1
        private const val ITEMS_PER_PAGE = 5

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    return OrderViewModel(cartRepository) as T
                }
            }
    }
}
