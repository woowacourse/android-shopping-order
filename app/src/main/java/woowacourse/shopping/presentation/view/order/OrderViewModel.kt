package woowacourse.shopping.presentation.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.common.model.CartProductUiModel
import woowacourse.shopping.presentation.common.model.FetchPageDirection
import woowacourse.shopping.presentation.common.model.toCartItemUiModel
import woowacourse.shopping.presentation.common.util.MutableSingleLiveData
import woowacourse.shopping.presentation.common.util.SingleLiveData
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

    private val _orderProducts = MutableLiveData<List<CartProductUiModel>>(emptyList())
    val orderProducts get() = _orderProducts.value.orEmpty()

    val totalOrderPrice: LiveData<Int> =
        _orderProducts.map { it.sumOf { product -> product.totalPrice } }

    val totalOrderCount: LiveData<Int> =
        _orderProducts.map { it.sumOf { product -> product.quantity } }

    val isAllSelected: LiveData<Boolean> = _orderProducts.map { isCheckedAll(it) }

    init {
        fetchCartItems(FetchPageDirection.CURRENT)
    }

    override fun increaseQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .increaseQuantity(productId, QUANTITY_STEP)
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
                .onSuccess {
                    refreshCurrentPageItems(productId)
                    addOrderProductFromSuggestion(productId)
                }
        }
    }

    override fun decreaseQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .decreaseQuantity(productId, QUANTITY_STEP)
                .onSuccess { refreshCurrentPageItems(productId) }
                .onFailure { postToastEvent(CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    override fun onDeleteProduct(cartId: Long) {
        viewModelScope.launch {
            cartRepository
                .deleteCartProduct(cartId)
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

        viewModelScope.launch {
            cartRepository
                .fetchCartProducts(targetPage, ITEMS_PER_PAGE)
                .onSuccess { handleCartItemsFetchSuccess(it, targetPage) }
                .onFailure { postToastEvent(CartMessageEvent.FETCH_CART_ITEMS_FAILURE) }
        }
    }

    fun toggleSelectAll() {
        val isCheckedAll = isAllSelected.value ?: false
        setSelectedCurrentCartProducts(!isCheckedAll)

        if (isCheckedAll) {
            _orderProducts.value = emptyList()
            return
        }

        val cartProducts = getAllCartProducts().map { it.toCartItemUiModel() }
        _orderProducts.value = cartProducts
    }

    private fun addOrderProductFromSuggestion(productId: Long) {
        if (getCurrentOrderProducts().any { it.productId == productId }) return
        toggleOrderProductSelection(productId)
    }

    private fun applySelectionState(cartProducts: List<CartProductUiModel>): List<CartProductUiModel> {
        val selectedProductIds = getCurrentOrderProducts().map { it.productId }
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

    private fun findRemovedProductIds(newItems: List<CartProductUiModel>): List<Long> {
        val newProductIds = newItems.map { it.productId }
        return getCurrentCartItems().map { it.productId }.filter { it !in newProductIds }
    }

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

    private fun handleQuantityRefreshSuccess(
        targetProductId: Long,
        pageableItem: PageableItem<CartProduct>,
    ) {
        val itemsWithSelectionState = convertPageableToUiModels(pageableItem)
        updateOrderProductsWithLatestData(targetProductId)
        val removedProductIds = findRemovedProductIds(itemsWithSelectionState)
        updateUiStateAfterRefresh(
            itemsWithSelectionState,
            pageableItem.hasMore,
            removedProductIds,
        )
    }

    private fun convertPageableToUiModels(pageableItem: PageableItem<CartProduct>): List<CartProductUiModel> {
        val uiModels = convertToUiModels(pageableItem.items)
        return applySelectionState(uiModels)
    }

    private fun updateUiStateAfterRefresh(
        itemsWithSelectionState: List<CartProductUiModel>,
        hasMore: Boolean,
        removedProductIds: List<Long>,
    ) {
        updateCartItemsAfterRefresh(itemsWithSelectionState, hasMore)
        removeDeletedProductsFromOrder(removedProductIds)
    }

    private fun isCheckedAll(currentOrderProducts: List<CartProductUiModel>): Boolean {
        if (currentOrderProducts.isEmpty()) return false
        val cartProducts = getAllCartProducts()
        return cartProducts.all { cartProduct ->
            currentOrderProducts.any { it.productId == cartProduct.product.id }
        }
    }

    private fun refreshCurrentPageItems(targetProductId: Long) {
        val currentPage = getCurrentPage()

        viewModelScope.launch {
            cartRepository
                .fetchCartProducts(currentPage, ITEMS_PER_PAGE)
                .onSuccess { handleQuantityRefreshSuccess(targetProductId, it) }
                .onFailure { postToastEvent(CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun removeDeletedProductsFromOrder(deletedProductIds: List<Long>) {
        if (deletedProductIds.isEmpty()) return
        val filteredProducts =
            getCurrentOrderProducts().filter { it.productId !in deletedProductIds }
        _orderProducts.value = filteredProducts
    }

    private fun removeProductFromOrderList(cartId: Long) {
        val updatedProducts = getCurrentOrderProducts().toMutableList()
        updatedProducts.removeIf { it.cartId == cartId }
        _orderProducts.value = updatedProducts
    }

    private fun setSelectedCurrentCartProducts(isSelected: Boolean) {
        val updatedCartProducts = getCurrentCartItems().map { it.copy(isSelected = isSelected) }
        _cartProducts.value = updatedCartProducts
    }

    private fun toggleOrderProductSelection(productId: Long) {
        val updatedProducts = toggleProductInOrderList(getCurrentOrderProducts(), productId)
        _orderProducts.value = (updatedProducts)
    }

    private fun toggleProductInOrderList(
        currentProducts: List<CartProductUiModel>,
        productId: Long,
    ): List<CartProductUiModel> {
        val mutableProducts = currentProducts.toMutableList()
        val wasRemoved = mutableProducts.removeIf { it.productId == productId }

        if (!wasRemoved) {
            val cartProduct =
                runBlocking { cartRepository.findCartProductByProductId(productId).getOrNull() }
            cartProduct?.let { mutableProducts.add(it.toCartItemUiModel()) }
        }

        return mutableProducts
    }

    private fun updateCartItemsAfterRefresh(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
    ) {
        if (items.isEmpty()) {
            fetchCartItems(FetchPageDirection.PREVIOUS)
            return
        }

        _cartProducts.value = items
        _hasMore.value = hasMore
    }

    private fun updateCartItemsState(
        items: List<CartProductUiModel>,
        hasMore: Boolean,
        page: Int,
    ) {
        _cartProducts.value = items
        _hasMore.value = hasMore
        _page.value = page
    }

    private fun updateOrderProductsWithLatestData(targetProductId: Long) {
        val targetCartProduct =
            runBlocking { cartRepository.findCartProductByProductId(targetProductId).getOrNull() }
        val currentOrderProducts = getCurrentOrderProducts().toMutableList()

        if (targetCartProduct == null) {
            removeOrderProduct(currentOrderProducts, targetProductId)
            return
        }

        replaceOrderProductWithLatest(currentOrderProducts, targetCartProduct)
    }

    private fun removeOrderProduct(
        currentOrderProducts: MutableList<CartProductUiModel>,
        targetProductId: Long,
    ) {
        currentOrderProducts.removeIf { it.productId == targetProductId }
        _orderProducts.value = (currentOrderProducts)
    }

    private fun replaceOrderProductWithLatest(
        currentOrderProducts: MutableList<CartProductUiModel>,
        updatedOrderProduct: CartProduct,
    ) {
        val updatedProducts =
            currentOrderProducts.map {
                if (it.productId != updatedOrderProduct.product.id) return@map it
                updatedOrderProduct.toCartItemUiModel()
            }
        _orderProducts.value = updatedProducts
    }

    private fun determineFetchDirectionAfterDeletion(deletedCartId: Long): FetchPageDirection {
        val currentItems = getCurrentCartItems()
        val isLastItemOnPage =
            currentItems.size == 1 && currentItems.first().cartId == deletedCartId
        return if (isLastItemOnPage) FetchPageDirection.PREVIOUS else FetchPageDirection.CURRENT
    }

    private fun getAllCartProducts(): List<CartProduct> = runBlocking { cartRepository.getAllCartProducts().getOrNull().orEmpty() }

    private fun getCurrentCartItems(): List<CartProductUiModel> = _cartProducts.value.orEmpty()

    private fun getCurrentOrderProducts(): List<CartProductUiModel> = _orderProducts.value.orEmpty()

    private fun getCurrentPage(): Int = _page.value ?: DEFAULT_PAGE

    private fun startLoading() {
        _isLoading.value = true
    }

    private fun stopLoading() {
        _isLoading.value = false
    }

    private fun postToastEvent(event: CartMessageEvent) {
        _toastEvent.setValue(event)
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
