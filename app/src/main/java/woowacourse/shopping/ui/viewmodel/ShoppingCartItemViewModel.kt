package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.ui.pagination.ShoppingCartPageStateHolder
import woowacourse.shopping.ui.state.ShoppingCartState

class ShoppingCartItemViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
) : ViewModel() {
    private val shoppingCartPageStateHolder =
        ShoppingCartPageStateHolder(shoppingCartItems = emptyList())

    private val _uiState = MutableStateFlow(ShoppingCartState())
    val uiState: StateFlow<ShoppingCartState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ShoppingCartEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ShoppingCartEvent> = _event.asSharedFlow()

    private val _shoppingCartItems: MutableStateFlow<ShoppingCartItemsState> =
        MutableStateFlow(createShoppingCartItemsState(emptyList()))
    val shoppingCartItems: StateFlow<ShoppingCartItemsState> = _shoppingCartItems.asStateFlow()

    private val _selectedProductIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedProductIds: StateFlow<Set<Long>> = _selectedProductIds.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { latestShoppingCartItems ->
                shoppingCartPageStateHolder.updateItems(latestShoppingCartItems)
                publishCurrentPageState(items = latestShoppingCartItems)
            }
        }
    }

    fun requestCartItems() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            runCatching {
                shoppingCartRepository.getShoppingItems()
            }.onSuccess { items ->
                shoppingCartPageStateHolder.updateItems(items)
                publishCurrentPageState(
                    items = items,
                    isLoading = false,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "장바구니 조회 실패",
                    )
                }
            }
        }
    }

    fun addOrIncreaseByProductId(
        productId: Long,
    ) {
        viewModelScope.launch {
            shoppingCartRepository.addIfAbsent(productId)
            shoppingItemRepository.plusQuantity(productId = productId)
        }
    }

    fun decreaseByProductId(
        productId: Long,
    ) {
        viewModelScope.launch {
            val currentQuantity = shoppingItemRepository.getQuantity(productId)
            if (currentQuantity == 0) {
                return@launch
            }
            shoppingItemRepository.minusQuantity(productId = productId)
            if (currentQuantity == 1) {
                shoppingCartRepository.removeByProductId(productId)
            }
        }
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        addOrIncreaseByProductId(productId = productId)
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        decreaseByProductId(productId = shoppingCartItem.product.id)
    }

    fun setShoppingCartProductSelection(
        productId: Long,
        isSelected: Boolean,
    ) {
        val validProductIds =
            _shoppingCartItems.value.items.map { shoppingCartItem -> shoppingCartItem.product.id }.toSet()
        if (productId !in validProductIds) return
        _selectedProductIds.value =
            _selectedProductIds.value.toMutableSet().apply {
                if (isSelected) {
                    add(productId)
                } else {
                    remove(productId)
                }
            }
        publishCurrentPageState()
    }

    fun setShoppingCartProductsSelection(
        productIds: List<Long>,
        isSelected: Boolean,
    ) {
        val validProductIds =
            _shoppingCartItems.value.items.map { shoppingCartItem -> shoppingCartItem.product.id }.toSet()
        val targetProductIds = productIds.toSet().intersect(validProductIds)
        if (isSelected) {
            _selectedProductIds.value = targetProductIds
            publishCurrentPageState()
            return
        }
        _selectedProductIds.value = _selectedProductIds.value - targetProductIds
        publishCurrentPageState()
    }

    fun moveToPreviousPage() {
        shoppingCartPageStateHolder.beforePage()
        publishCurrentPageState()
    }

    fun moveToNextPage() {
        shoppingCartPageStateHolder.nextPage()
        publishCurrentPageState()
    }

    fun onBackClick() {
        _event.tryEmit(ShoppingCartEvent.NavigateBack)
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        viewModelScope.launch {
            shoppingCartRepository.remove(shoppingCartItem)
            resetQuantity(shoppingCartItem.product.id)
        }
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int =
        shoppingCartItem.getProductQuantityPrice()

    fun refresh() {
        publishCurrentPageState()
    }

    private fun publishCurrentPageState(
        items: List<ShoppingCartItem> = _shoppingCartItems.value.items,
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
    ) {
        val validSelectedProductIds =
            _selectedProductIds.value.intersect(items.map { shoppingCartItem -> shoppingCartItem.product.id }.toSet())
        _selectedProductIds.value = validSelectedProductIds

        val shoppingCartItemsState = createShoppingCartItemsState(items)
        _shoppingCartItems.value = shoppingCartItemsState

        val selectedItemCount = validSelectedProductIds.size
        _uiState.value =
            _uiState.value.copy(
                items = shoppingCartItemsState.items,
                selectedProductIds = validSelectedProductIds,
                isLoading = isLoading,
                errorMessage = errorMessage,
                currentPage = shoppingCartItemsState.currentPage,
                selectedItemCount = selectedItemCount,
                canOrder = selectedItemCount > 0 && !isLoading,
                canMoveToPreviousPage = shoppingCartItemsState.canMoveToPreviousPage,
                canMoveToNextPage = shoppingCartItemsState.canMoveToNextPage,
            )
    }

    private suspend fun resetQuantity(productId: Long) {
        val currentQuantity = shoppingItemRepository.getQuantity(productId)
        if (currentQuantity == 0) {
            return
        }
        shoppingItemRepository.minusQuantity(productId, currentQuantity)
    }

    private fun createShoppingCartItemsState(items: List<ShoppingCartItem>): ShoppingCartItemsState =
        ShoppingCartItemsState(
            items = items,
            pagedItems = shoppingCartPageStateHolder.getItems(),
            currentPage = shoppingCartPageStateHolder.currentPage,
            canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
            canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
        )

    data class ShoppingCartItemsState(
        val items: List<ShoppingCartItem>,
        val pagedItems: List<ShoppingCartItem> = emptyList(),
        val currentPage: Int = INITIAL_PAGE,
        val canMoveToPreviousPage: Boolean = false,
        val canMoveToNextPage: Boolean = false,
    )

    fun getTotalPrice(
        shoppingCartItems: List<ShoppingCartItem>,
        selectedProductIds: Set<Long>,
    ): Int =
        shoppingCartItems
            .filter { it.product.id in selectedProductIds }
            .sumOf { it.getProductQuantityPrice() }

    companion object {
        private const val INITIAL_PAGE = 0
    }

    sealed interface ShoppingCartEvent {
        data object NavigateBack : ShoppingCartEvent
    }
}
