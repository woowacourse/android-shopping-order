package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.network.NetworkStatusMonitor
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore
import woowacourse.shopping.ui.pagination.ProductPageStateHolder

class ProductListViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
    private val visitStore: VisitStore = ShoppingApplication.visitStore,
    private val networkStatusMonitor: NetworkStatusMonitor = ShoppingApplication.networkStatusMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ProductListEvent> = _event.asSharedFlow()

    private val productPageStateHolder = ProductPageStateHolder(shoppingItems = emptyList())
    private var allShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value
    private var networkConnected: Boolean = networkStatusMonitor.isConnected.value

    init {
        productPageStateHolder.updateItems(allShoppingItems)
        updateUiState()
        observeSources()
    }

    fun loadNextPage() {
        productPageStateHolder.nextPage()
        updateUiState()
    }

    fun onProductClick(productId: Long) {
        _event.tryEmit(ProductListEvent.NavigateToDetailProduct(productId))
    }

    fun onRecentViewedProductClick(productId: Long) {
        _event.tryEmit(ProductListEvent.NavigateToDetailProduct(productId))
    }

    fun onNavigateToCartClick() {
        _event.tryEmit(ProductListEvent.NavigateToShoppingCart)
    }

    fun addProductToCart(shoppingItem: ShoppingItem) {
        launchNow {
            increaseQuantity(shoppingItem.getProductId())
        }
    }

    fun increaseProductQuantity(shoppingItem: ShoppingItem) {
        launchNow {
            increaseQuantity(shoppingItem.getProductId())
        }
    }

    fun decreaseProductQuantity(shoppingItem: ShoppingItem) {
        launchNow {
            val productId = shoppingItem.getProductId()
            val currentQuantity = shoppingItemRepository.getQuantity(productId)
            if (currentQuantity == 0) {
                return@launchNow
            }
            shoppingItemRepository.minusQuantity(productId)
            if (currentQuantity == 1) {
                shoppingCartRepository.removeByProductId(productId)
            }
        }
    }

    private suspend fun ensureShoppingCartContains(productId: Long) {
        if (shoppingCartRepository.containsProduct(productId)) {
            return
        }
        val sourceItem = shoppingItemRepository.getShoppingItemOrNull(productId) ?: return
        shoppingCartRepository.add(sourceItem)
    }

    private suspend fun increaseQuantity(productId: Long) {
        ensureShoppingCartContains(productId)
        shoppingItemRepository.plusQuantity(productId)
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                allShoppingItems = latestShoppingItems
                productPageStateHolder.updateItems(latestShoppingItems)
                updateUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect { latestRecentViewedIds ->
                recentViewedProductIds = latestRecentViewedIds
                updateUiState()
            }
        }
        viewModelScope.launch {
            networkStatusMonitor.isConnected.collect { isConnected ->
                networkConnected = isConnected
                updateUiState()
            }
        }
    }

    private fun launchNow(block: suspend () -> Unit) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            block()
        }
    }

    private fun updateUiState() {
        val shoppingItemByProductId = allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        _uiState.value =
            ProductListUiState(
                shoppingItems = productPageStateHolder.getItems(),
                recentViewedShoppingItems = recentViewedProductIds.mapNotNull { productId -> shoppingItemByProductId[productId] },
                shoppingCartTotalCount = allShoppingItems.sumOf { shoppingItem -> shoppingItem.getQuantity() },
                isNetworkConnected = networkConnected,
                canLoadNextPage = productPageStateHolder.canMoveToNextPage(),
            )
    }

    data class ProductListUiState(
        val shoppingItems: List<ShoppingItem> = emptyList(),
        val recentViewedShoppingItems: List<ShoppingItem> = emptyList(),
        val shoppingCartTotalCount: Int = 0,
        val isNetworkConnected: Boolean = true,
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
