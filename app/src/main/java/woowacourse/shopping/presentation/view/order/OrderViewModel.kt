package woowacourse.shopping.presentation.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.di.provider.UseCaseProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.model.FetchPageDirection
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.order.cart.event.CartStateListener
import kotlin.math.max

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
) : ViewModel(),
    CartStateListener {
    private val _toastOrderEvent = MutableSingleLiveData<OrderMessageEvent>()
    val toastOrderEvent: SingleLiveData<OrderMessageEvent> = this._toastOrderEvent

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

    val totalPrice: LiveData<Int> =
        selectedCartItems.map { it.sumOf { cartProduct -> cartProduct.totalPrice } }

    val totalCount: LiveData<Int> =
        selectedCartItems.map { selectedCartItems -> selectedCartItems.sumOf { it.quantity } }

    private val _isCheckAll = MutableLiveData<Boolean>()
    val isCheckAll: LiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSource(selectedCartItems) { selectedCartItems ->
                value = selectedCartItems.size ==
                    cartRepository
                        .fetchAllCartItems()
                        .getOrDefault(emptyList())
                        .size
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
                .onFailure { postFailureOrderEvent(OrderMessageEvent.FETCH_CART_ITEMS_FAILURE) }
        }
    }

    override fun onDeleteProduct(cartId: Long) {
        deleteCartItem(cartId)
    }

    override fun increaseQuantity(productId: Long) {
        increaseProductQuantity(productId)
    }

    override fun decreaseQuantity(productId: Long) {
        decreaseProductQuantity(productId)
    }

    override fun onCheckOrder(cartId: Long) {
        switchCartItemSelection(cartId)
    }

    override fun onSwitchAllOrder() {
        selectCurrentPageCartProduct()
    }

    private fun deleteCartItem(cartId: Long) {
        cartRepository.deleteCartItem(cartId) { result ->
            result
                .onSuccess { handleFetchCartItemDeleted(cartId) }
                .onFailure { postFailureOrderEvent(OrderMessageEvent.DELETE_CART_ITEM_FAILURE) }
        }
    }

    private fun increaseProductQuantity(productId: Long) {
        viewModelScope.launch {
            increaseCartProductQuantityUseCase(
                productId,
                QUANTITY_STEP,
            ).onSuccess { refreshFetchItem() }
                .onFailure { postFailureOrderEvent(OrderMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun decreaseProductQuantity(productId: Long) {
        decreaseProductQuantityUseCase(productId, QUANTITY_STEP) { result ->
            result
                .onSuccess { refreshFetchItem() }
                .onFailure { postFailureOrderEvent(OrderMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE) }
        }
    }

    private fun switchCartItemSelection(cartId: Long) {
        val selectedItems = selectedCartItems.value.orEmpty()
        if (selectedItems.find { it.cartId == cartId } != null) {
            selectedCartItems.value = selectedItems.filter { it.cartId != cartId }
            return
        }
        val foundCartProduct = this.cartItems.value?.find { it.cartId == cartId } ?: return
        selectedCartItems.value = selectedItems + foundCartProduct
    }

    private fun selectCurrentPageCartProduct() {
        _isCheckAll.value = !(_isCheckAll.value ?: false)
        val checked = (_isCheckAll.value ?: false)
        if (checked) {
            selectedCartItems.value =
                cartRepository
                    .fetchAllCartItems()
                    .getOrDefault(emptyList())
                    .map { it.toCartItemUiModel() }
            return
        }
        selectedCartItems.value = emptyList()
    }

    fun orderCartItems(purchaseSuggestionIds: List<Long>) {
        val selectedItemIds = selectedCartItems.value.orEmpty().map { it.cartId }
        val purchaseCartIds = selectedItemIds + purchaseSuggestionIds

        orderRepository.addOrder(purchaseCartIds.map { it.toString() }) { result ->
            result
                .onSuccess { deleteSelectedCartItems(purchaseCartIds) }
                .onFailure { postFailureOrderEvent(OrderMessageEvent.ORDER_CART_ITEMS_FAILURE) }
        }
    }

    private fun deleteSelectedCartItems(purchaseCartIds: List<Long>) {
        purchaseCartIds.map {
            cartRepository.deleteCartItem(it) {
            }
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

    private fun refreshFetchItem() {
        val newPage = calculatePage(FetchPageDirection.CURRENT)
        cartRepository.fetchCartItems(newPage, limit) { result ->
            result
                .onSuccess { onRefreshProductQuantitySuccess(it) }
                .onFailure { postFailureOrderEvent(OrderMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE) }
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

    private fun postFailureOrderEvent(event: OrderMessageEvent) {
        this._toastOrderEvent.postValue(event)
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
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val orderRepository = RepositoryProvider.orderRepository
                    val increaseCartProductQuantityUseCase =
                        UseCaseProvider.increaseCartProductQuantityUseCase
                    val decreaseProductQuantityUseCase =
                        UseCaseProvider.decreaseProductQuantityUseCase
                    return OrderViewModel(
                        cartRepository,
                        orderRepository,
                        increaseCartProductQuantityUseCase,
                        decreaseProductQuantityUseCase,
                    ) as T
                }
            }
    }
}
