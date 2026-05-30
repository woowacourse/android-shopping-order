package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
) : ViewModel() {
    private val shoppingCartPageStateHolder =
        ShoppingCartPageStateHolder(shoppingCartItems = emptyList())

    private val _uiState = MutableStateFlow(ShoppingCartUiState())
    val uiState: StateFlow<ShoppingCartUiState> = _uiState.asStateFlow()
    private val _screenState = MutableStateFlow<ShoppingCartScreenState>(ShoppingCartScreenState.Loading)
    val screenState: StateFlow<ShoppingCartScreenState> = _screenState.asStateFlow()

    private var isCartRequestInProgress: Boolean = false

    init {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { latestShoppingCartItems ->
                shoppingCartPageStateHolder.updateItems(latestShoppingCartItems)
                syncLocalShoppingCartItems(latestShoppingCartItems)
            }
        }
    }

    fun moveToPreviousPage() {
        shoppingCartPageStateHolder.beforePage()
        refreshUiState()
    }

    fun moveToNextPage() {
        shoppingCartPageStateHolder.nextPage()
        refreshUiState()
        prefetchNextRemotePageIfNeeded()
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    fun requestCartItems(force: Boolean = false) {
        if (isCartRequestInProgress) return
        _screenState.value = ShoppingCartScreenState.Loading
        viewModelScope.launch {
            isCartRequestInProgress = true
            try {
                shoppingCartRepository.requestCartItems(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_SIZE,
                    sort = null,
                    force = force,
                )
                setContentState()
            } catch (throwable: Throwable) {
                publishCartError(
                    throwable = throwable,
                    defaultMessage = "장바구니를 불러오지 못했습니다.",
                )
            } finally {
                isCartRequestInProgress = false
            }
        }
    }

    fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int = DEFAULT_QUANTITY,
        onSuccess: (() -> Unit)? = null,
    ) {
        if (amount <= 0) return
        executeCartMutation(
            defaultMessage = "장바구니 수량을 변경하지 못했습니다.",
            onSuccess = onSuccess,
        ) {
            shoppingCartRepository.addOrIncreaseByProductId(
                productId = productId,
                amount = amount,
            )
        }
    }

    fun decreaseByProductId(productId: Long) {
        executeCartMutation(defaultMessage = "장바구니 수량을 변경하지 못했습니다.") {
            shoppingCartRepository.decreaseByProductId(productId = productId)
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        executeCartMutation(defaultMessage = "장바구니 상품을 삭제하지 못했습니다.") {
            val productId = shoppingCartItem.product.id
            shoppingCartRepository.removeByProductId(productId = productId)
        }
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        addOrIncreaseByProductId(productId = productId, amount = 1)
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        decreaseByProductId(productId = shoppingCartItem.product.id)
    }

    fun setShoppingCartProductSelection(
        productId: Long,
        isSelected: Boolean,
    ) {
        val validProductIds = getValidProductIds()
        if (productId !in validProductIds) return
        val nextSelectedProductIds =
            _uiState.value.selectedProductIds.toMutableSet().apply {
                if (isSelected) {
                    add(productId)
                } else {
                    remove(productId)
                }
            }
        refreshUiState(selectedProductIds = nextSelectedProductIds)
    }

    fun setShoppingCartProductsSelection(
        productIds: List<Long>,
        isSelected: Boolean,
    ) {
        val validProductIds = getValidProductIds()
        val targetProductIds = productIds.toSet().intersect(validProductIds)
        if (isSelected) {
            refreshUiState(selectedProductIds = targetProductIds)
            return
        }
        refreshUiState(selectedProductIds = _uiState.value.selectedProductIds - targetProductIds)
    }

    private fun syncLocalShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        val validProductIds = shoppingCartItems.map { it.product.id }.toSet()
        val selectedProductIds = _uiState.value.selectedProductIds.intersect(validProductIds)
        refreshUiState(
            shoppingCartItems = shoppingCartItems,
            selectedProductIds = selectedProductIds,
        )
        if (!isCartRequestInProgress && _screenState.value is ShoppingCartScreenState.Loading) {
            setContentState()
        }
    }

    private fun getValidProductIds(): Set<Long> =
        _uiState.value.shoppingCartItems
            .map { shoppingCartItem -> shoppingCartItem.product.id }
            .toSet()

    private fun prefetchNextRemotePageIfNeeded() {
        if (shoppingCartPageStateHolder.canMoveToNextPage()) return

        viewModelScope.launch {
            runCatching {
                val nextLocalPage = shoppingCartPageStateHolder.currentPage + 1
                val targetRemotePage = getRemotePageIndexByLocalPage(nextLocalPage)
                shoppingCartRepository.requestCartItems(
                    page = targetRemotePage,
                    size = DEFAULT_SIZE,
                    sort = null,
                    force = false,
                )
            }.onFailure { throwable ->
                publishCartError(
                    throwable = throwable,
                    defaultMessage = "장바구니를 불러오지 못했습니다.",
                )
            }
        }
    }

    private fun getRemotePageIndexByLocalPage(localPage: Int): Int {
        val safeLocalPage = localPage.coerceAtLeast(DEFAULT_PAGE)
        val requiredItemCount = (safeLocalPage + 1) * ShoppingCartPageStateHolder.PAGE_ITEM_SIZE
        return (requiredItemCount - 1).coerceAtLeast(0) / DEFAULT_SIZE
    }

    private fun executeCartMutation(
        defaultMessage: String,
        onSuccess: (() -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        if (_screenState.value is ShoppingCartScreenState.Error) {
            setContentState()
        }
        viewModelScope.launch {
            runCatching {
                block()
            }.onSuccess {
                setContentState()
                onSuccess?.invoke()
            }.onFailure { throwable ->
                publishCartError(
                    throwable = throwable,
                    defaultMessage = defaultMessage,
                )
            }
        }
    }

    private fun publishCartError(
        throwable: Throwable,
        defaultMessage: String,
    ) {
        _screenState.value =
            ShoppingCartScreenState.Error(
                message =
                    throwable
                        .toApiFailure()
                        .toUserMessage(defaultMessage = defaultMessage),
            )
    }

    private fun setContentState() {
        _screenState.value = ShoppingCartScreenState.Content
    }

    private fun refreshUiState(
        shoppingCartItems: List<ShoppingCartItem> = _uiState.value.shoppingCartItems,
        selectedProductIds: Set<Long> = _uiState.value.selectedProductIds,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingCartItems = shoppingCartItems,
                selectedProductIds = selectedProductIds,
                pagedItems = shoppingCartPageStateHolder.getItems(),
                currentPage = shoppingCartPageStateHolder.currentPage,
                canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
                canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
            )
        }
    }

    sealed interface ShoppingCartScreenState {
        data object Loading : ShoppingCartScreenState

        data object Content : ShoppingCartScreenState

        data class Error(
            val message: String,
        ) : ShoppingCartScreenState
    }

    data class ShoppingCartUiState(
        val shoppingCartItems: List<ShoppingCartItem> = emptyList(),
        val selectedProductIds: Set<Long> = emptySet(),
        val pagedItems: List<ShoppingCartItem> = emptyList(),
        val currentPage: Int = DEFAULT_PAGE,
        val canMoveToPreviousPage: Boolean = false,
        val canMoveToNextPage: Boolean = false,
    )

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val DEFAULT_QUANTITY = 1
    }
}
