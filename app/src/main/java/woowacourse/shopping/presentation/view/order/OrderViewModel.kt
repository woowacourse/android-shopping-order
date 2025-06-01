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
import woowacourse.shopping.presentation.view.order.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.view.order.cart.event.CartMessageEvent
import woowacourse.shopping.presentation.view.order.suggestion.adapter.SuggestionAdapter
import kotlin.math.max

class OrderViewModel(
    private val cartRepository: CartRepository,
) : ViewModel(),
    SuggestionAdapter.SuggestionEventListener,
    CartAdapter.CartEventListener {
    private val _toastEvent = MutableSingleLiveData<CartMessageEvent>()
    val toastEvent: SingleLiveData<CartMessageEvent> = _toastEvent

    private val _cartProducts = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val cartProducts: LiveData<List<CartProductUiModel>> = _cartProducts

    private val _page = MutableLiveData(DEFAULT_PAGE)
    val page: LiveData<Int> = _page.map { it + 1 }

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val orderProducts = MutableLiveData<List<CartProductUiModel>>(emptyList())

    val orderTotalPrice: LiveData<Int> =
        orderProducts.map { it.sumOf { product -> product.totalPrice } }

    val orderTotalCount: LiveData<Int> =
        orderProducts.map { it.sumOf { product -> product.quantity } }

    val isCheckedAll: LiveData<Boolean> = orderProducts.map { isCheckedAll(it) }

    init {
        fetchCartItems(FetchPageDirection.CURRENT)
    }

    override fun increaseQuantity(productId: Long) {
        cartRepository.increaseQuantity(productId, QUANTITY_STEP) { result ->
            result
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
                .onSuccess {
                    refreshCurrentPageItems()
                    addOrderProductFromSuggestion(productId)
                }
        }
    }

    override fun decreaseQuantity(productId: Long) {
        cartRepository.decreaseQuantity(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshCurrentPageItems() }
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    override fun onDeleteProduct(cartId: Long) {
        cartRepository.deleteCartProduct(cartId) { result ->
            result
                .onSuccess { handleCartItemDeletionSuccess(cartId) }
                .onFailure { postToastEvent(CartMessageEvent.DELETE_CART_ITEM_FAILURE) }
        }
    }

    override fun onSelectOrderProduct(productId: Long) {
        toggleOrderProductSelection(productId)
    }

    fun fetchCartItems(direction: FetchPageDirection) {
        val targetPage = calculateTargetPage(direction)
        startLoading()

        cartRepository.fetchCartProducts(targetPage, ITEMS_PER_PAGE) { result ->
            result
                .onSuccess { handleCartItemsFetchSuccess(it, targetPage) }
                .onFailure { postToastEvent(CartMessageEvent.FETCH_CART_ITEMS_FAILURE) }
        }
    }

    fun toggleAllOrderProductSelection() {
        val isCheckedAll = isCheckedAll.value ?: false
        setSelectedCurrentCartProducts(!isCheckedAll)

        if (isCheckedAll) {
            orderProducts.postValue(emptyList())
            return
        }

        val cartProducts = getAllCartProducts().map { it.toCartItemUiModel() }
        orderProducts.postValue(cartProducts)
    }

    private fun addOrderProductFromSuggestion(productId: Long) {
        if (getCurrentOrderProducts().any { it.productId == productId }) return
        toggleOrderProductSelection(productId)
    }

    private fun applySelectionState(cartProducts: List<CartProductUiModel>): List<CartProductUiModel> {
        updateOrderProductsWithLatestData(cartProducts)
        val selectedProductIds = getSelectedProductIds()

        return cartProducts.map { it.copy(isSelected = selectedProductIds.contains(it.productId)) }
    }

    private fun calculateTargetPage(direction: FetchPageDirection): Int {
        val currentPage = getCurrentPage()
        return when (direction) {
            FetchPageDirection.PREVIOUS -> max(DEFAULT_PAGE, currentPage - PAGE_STEP)
            FetchPageDirection.CURRENT -> currentPage
            FetchPageDirection.NEXT -> currentPage + PAGE_STEP
        }
    }

    private fun convertToUiModels(cartProducts: List<CartProduct>): List<CartProductUiModel> = cartProducts.map { it.toCartItemUiModel() }

    private fun findRemovedProductIds(newItems: List<CartProductUiModel>): Set<Long> {
        val newProductIds = newItems.map { it.productId }.toSet()
        return getCurrentCartItems().map { it.productId }.filter { it !in newProductIds }.toSet()
    }

    private fun getAllCartProducts(): List<CartProduct> = cartRepository.getAllCartProducts().getOrNull().orEmpty()

    private fun getCurrentCartItems(): List<CartProductUiModel> = _cartProducts.value.orEmpty()

    private fun getCurrentOrderProducts(): List<CartProductUiModel> = orderProducts.value.orEmpty()

    private fun getCurrentPage(): Int = _page.value ?: DEFAULT_PAGE

    private fun getSelectedProductIds(): Set<Long> = getCurrentOrderProducts().map { it.productId }.toSet()

    private fun handleCartItemDeletionSuccess(deletedCartId: Long) {
        removeProductFromOrderList(deletedCartId)
        val fetchDirection = determineFetchDirectionAfterDeletion(deletedCartId)
        fetchCartItems(fetchDirection)
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

    private fun handleQuantityRefreshSuccess(pageableItem: PageableItem<CartProduct>) {
        val uiModels = convertToUiModels(pageableItem.items)
        val itemsWithSelectionState = applySelectionState(uiModels)
        val removedProductIds = findRemovedProductIds(itemsWithSelectionState)
        updateCartItemsAfterRefresh(itemsWithSelectionState, pageableItem.hasMore)
        removeDeletedProductsFromOrder(removedProductIds)
    }

    private fun isCheckedAll(currentOrderProducts: List<CartProductUiModel>): Boolean {
        val cartProducts = getAllCartProducts()
        return cartProducts.all { cartProduct ->
            currentOrderProducts.any { it.productId == cartProduct.product.id }
        }
    }

    private fun postToastEvent(event: CartMessageEvent) {
        _toastEvent.postValue(event)
    }

    private fun refreshCurrentPageItems() {
        val currentPage = getCurrentPage()
        cartRepository.fetchCartProducts(currentPage, ITEMS_PER_PAGE) { result ->
            result
                .onSuccess { handleQuantityRefreshSuccess(it) }
                .onFailure { postToastEvent(CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun removeDeletedProductsFromOrder(deletedProductIds: Set<Long>) {
        if (deletedProductIds.isEmpty()) return

        val filteredProducts =
            getCurrentOrderProducts().filter { it.productId !in deletedProductIds }
        if (filteredProducts.size != getCurrentOrderProducts().size) {
            orderProducts.postValue(filteredProducts)
        }
    }

    private fun removeProductFromOrderList(cartId: Long) {
        val updatedProducts =
            getCurrentOrderProducts().toMutableList().apply {
                removeIf { it.cartId == cartId }
            }
        orderProducts.postValue(updatedProducts)
    }

    private fun setSelectedCurrentCartProducts(isSelected: Boolean) {
        val updatedCartProducts = getCurrentCartItems().map { it.copy(isSelected = isSelected) }
        _cartProducts.postValue(updatedCartProducts)
    }

    private fun startLoading() {
        _isLoading.postValue(true)
    }

    private fun stopLoading() {
        _isLoading.postValue(false)
    }

    private fun toggleOrderProductSelection(productId: Long) {
        val updatedProducts = toggleProductInOrderList(getCurrentOrderProducts(), productId)
        orderProducts.postValue(updatedProducts)
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

    private fun updateCartItemsAfterRefresh(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
    ) {
        _cartProducts.postValue(items)
        _hasMore.postValue(hasMore)
    }

    private fun updateCartItemsState(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
        page: Int,
    ) {
        _cartProducts.postValue(items)
        _hasMore.postValue(hasMore)
        _page.postValue(page)
    }

    private fun updateOrderProductsWithLatestData(cartProducts: List<CartProductUiModel>) {
        val updatedProducts =
            getCurrentOrderProducts().map { orderProduct ->
                cartProducts.find { it.productId == orderProduct.productId } ?: orderProduct
            }
        orderProducts.postValue(updatedProducts)
    }

    private fun determineFetchDirectionAfterDeletion(deletedCartId: Long): FetchPageDirection {
        val currentItems = getCurrentCartItems()
        val isLastItemOnPage =
            currentItems.size == 1 && currentItems.first().cartId == deletedCartId
        return if (isLastItemOnPage) FetchPageDirection.PREVIOUS else FetchPageDirection.CURRENT
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
