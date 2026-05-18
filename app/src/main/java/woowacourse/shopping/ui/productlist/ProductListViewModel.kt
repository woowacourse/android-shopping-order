package woowacourse.shopping.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ProductListViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ProductListEvent> = _event.asSharedFlow()

    private val productPageStateHolder = ProductPageStateHolder(shoppingItems = emptyList())
    private var allShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value

    init {
        productPageStateHolder.updateItems(allShoppingItems)
        publishUiState()
        observeSources()
    }

    fun loadNextPage() {
        productPageStateHolder.nextPage()
        publishUiState()
    }

    fun onProductClick(productId: Long) {
        _event.tryEmit(ProductListEvent.NavigateToDetailProduct(productId))
    }

    fun onRecentViewedProductClick(productId: Long) {
        _event.tryEmit(
            ProductListEvent.NavigateToDetailProduct(
                productId = productId,
            ),
        )
    }

    fun onNavigateToCartClick() {
        _event.tryEmit(ProductListEvent.NavigateToShoppingCart)
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                allShoppingItems = latestShoppingItems
                productPageStateHolder.updateItems(latestShoppingItems)
                publishUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect { latestRecentViewedIds ->
                recentViewedProductIds = latestRecentViewedIds
                publishUiState()
            }
        }
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): ProductListUiState {
        val shoppingItemByProductId = allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        return ProductListUiState(
            shoppingItems = productPageStateHolder.getItems(),
            recentViewedShoppingItems = recentViewedProductIds.mapNotNull { productId -> shoppingItemByProductId[productId] },
            shoppingCartTotalCount = allShoppingItems.sumOf { shoppingItem -> shoppingItem.getQuantity() },
            canLoadNextPage = productPageStateHolder.canMoveToNextPage(),
        )
    }

    data class ProductListUiState(
        val shoppingItems: List<ShoppingItem> = emptyList(),
        val recentViewedShoppingItems: List<ShoppingItem> = emptyList(),
        val shoppingCartTotalCount: Int = 0,
        val canLoadNextPage: Boolean = false,
    )

    sealed interface ProductListEvent {
        data class NavigateToDetailProduct(
            val productId: Long,
            val showLastViewed: Boolean = true,
        ) : ProductListEvent

        data object NavigateToShoppingCart : ProductListEvent
    }
}
