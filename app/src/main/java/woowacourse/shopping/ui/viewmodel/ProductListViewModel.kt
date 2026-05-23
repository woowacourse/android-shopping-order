package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.mapper.toDomainProducts
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.network.NetworkStatusMonitor
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore
import woowacourse.shopping.ui.pagination.ProductPageStateHolder
import woowacourse.shopping.ui.state.ProductListUiState

class ProductListViewModel(
    private val productRetrofitRepository: ProductRetrofitRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val networkStatusMonitor: NetworkStatusMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ProductListEvent> = _event.asSharedFlow()

    private val productPageStateHolder = ProductPageStateHolder(shoppingItems = emptyList())
    private var allShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value
    private var networkConnected: Boolean = networkStatusMonitor.isConnected.value
    private var isLoading: Boolean = false
    private var errorMessage: String? = null

    init {
        productPageStateHolder.updateItems(allShoppingItems)
        publishUiState()
        observeSources()
    }

    fun requestProducts(size: Int) {
        if (isLoading) {
            return
        }
        val shouldShowLoading = allShoppingItems.isEmpty()
        isLoading = shouldShowLoading
        errorMessage = null
        publishUiState()

        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .requestProduct(size = size)
                    .toDomainProducts()
            }.onSuccess { products ->
                shoppingItemRepository.replaceProducts(products)
                isLoading = false
                errorMessage = null
                publishUiState()
            }.onFailure { throwable ->
                isLoading = false
                errorMessage = throwable.message ?: "상품 목록을 불러오지 못했습니다."
                publishUiState()
            }
        }
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
                showLastViewed = false,
            ),
        )
    }

    fun onNavigateToCartClick() {
        _event.tryEmit(ProductListEvent.NavigateToShoppingCart)
    }

    fun addProductToCart(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            increaseQuantity(shoppingItem.getProductId())
        }
    }

    fun increaseProductQuantity(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            increaseQuantity(shoppingItem.getProductId())
        }
    }

    fun decreaseProductQuantity(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            val productId = shoppingItem.getProductId()
            val currentQuantity = shoppingItemRepository.getQuantity(productId)
            if (currentQuantity == 0) {
                return@launch
            }
            shoppingItemRepository.minusQuantity(productId)
            if (currentQuantity == 1) {
                shoppingCartRepository.removeByProductId(productId)
            }
        }
    }

    private suspend fun increaseQuantity(productId: Long) {
        shoppingCartRepository.addIfAbsent(productId)
        shoppingItemRepository.plusQuantity(productId)
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
        viewModelScope.launch {
            networkStatusMonitor.isConnected.collect { isConnected ->
                networkConnected = isConnected
                publishUiState()
            }
        }
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): ProductListUiState {
        val shoppingItemByProductId =
            allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val hasError = errorMessage != null

        return ProductListUiState(
            shoppingItems = if (hasError) emptyList() else productPageStateHolder.getItems(),
            recentViewedShoppingItems =
                if (hasError) {
                    emptyList()
                } else {
                    recentViewedProductIds.mapNotNull { productId -> shoppingItemByProductId[productId] }
                },
            shoppingCartTotalCount =
                if (hasError) {
                    0
                } else {
                    allShoppingItems.sumOf { shoppingItem -> shoppingItem.getQuantity() }
                },
            isNetworkConnected = networkConnected,
            canLoadNextPage = !hasError && productPageStateHolder.canMoveToNextPage(),
            isLoading = isLoading,
            errorMessage = errorMessage,
        )
    }

    sealed interface ProductListEvent {
        data class NavigateToDetailProduct(
            val productId: Long,
            val showLastViewed: Boolean = true,
        ) : ProductListEvent

        data object NavigateToShoppingCart : ProductListEvent
    }
}
