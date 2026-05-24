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
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    fun requestCartItems(force: Boolean = false) {
        if (_uiState.value.isLoading) return
        refreshUiState(errorMessage = null)
        viewModelScope.launch {
            refreshUiState(isLoading = true)
            try {
                shoppingCartRepository.requestCartItems(
                    page = DEFAULT_PAGE,
                    size = DEFAULT_SIZE,
                    sort = null,
                    force = force,
                )
            } catch (throwable: Throwable) {
                publishCartError(
                    throwable = throwable,
                    defaultMessage = "장바구니를 불러오지 못했습니다.",
                )
            } finally {
                refreshUiState(isLoading = false)
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
    }

    private fun getValidProductIds(): Set<Long> =
        _uiState.value.shoppingCartItems
            .map { shoppingCartItem -> shoppingCartItem.product.id }
            .toSet()

    private fun executeCartMutation(
        defaultMessage: String,
        onSuccess: (() -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        refreshUiState(errorMessage = null)
        viewModelScope.launch {
            runCatching {
                block()
            }.onSuccess {
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
        refreshUiState(
            errorMessage =
                throwable
                    .toApiFailure()
                    .toUserMessage(defaultMessage = defaultMessage),
        )
    }

    private fun refreshUiState(
        shoppingCartItems: List<ShoppingCartItem> = _uiState.value.shoppingCartItems,
        selectedProductIds: Set<Long> = _uiState.value.selectedProductIds,
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingCartItems = shoppingCartItems,
                selectedProductIds = selectedProductIds,
                isLoading = isLoading,
                errorMessage = errorMessage,
                pagedItems = shoppingCartPageStateHolder.getItems(),
                currentPage = shoppingCartPageStateHolder.currentPage,
                canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
                canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
            )
        }
    }

    data class ShoppingCartUiState(
        val shoppingCartItems: List<ShoppingCartItem> = emptyList(),
        val selectedProductIds: Set<Long> = emptySet(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val pagedItems: List<ShoppingCartItem> = emptyList(),
        val currentPage: Int = INITIAL_PAGE,
        val canMoveToPreviousPage: Boolean = false,
        val canMoveToNextPage: Boolean = false,
    )

    private companion object {
        private const val INITIAL_PAGE = 0
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val DEFAULT_QUANTITY = 1
    }
}
