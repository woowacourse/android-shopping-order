package woowacourse.shopping.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ProductListViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val productRepository: ProductRetrofitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductListEvent>(capacity = Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val productPageStateHolder = ProductPageStateHolder(shoppingItems = emptyList())

    init {
        productPageStateHolder.updateItems(shoppingItemRepository.shoppingItems.value)
        refreshUiState()
        observeSources()
    }

    fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        category: String? = null,
        force: Boolean = false,
    ) {
        if (_uiState.value.isLoading) return

        refreshUiState(
            isLoading = true,
            errorMessage = null,
        )
        viewModelScope.launch {
            runCatching {
                productRepository.requestAllProducts(
                    startPage = page,
                    size = size,
                    category = category,
                    force = force,
                )
            }.onSuccess { loadedProducts ->
                if (loadedProducts == null) {
                    refreshUiState(
                        isLoading = false,
                        errorMessage = null,
                    )
                    return@onSuccess
                }
                shoppingItemRepository.replaceProducts(loadedProducts)
                productPageStateHolder.restoreCurrentPage(DEFAULT_PAGE)
                refreshUiState(
                    isLoading = false,
                    errorMessage = null,
                    hasLoadedProducts = true,
                )
            }.onFailure { throwable ->
                refreshUiState(
                    isLoading = false,
                    errorMessage =
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "상품 목록을 불러오지 못했습니다."),
                )
            }
        }
    }

    fun loadNextPage() {
        if (!productPageStateHolder.canMoveToNextPage()) return
        productPageStateHolder.nextPage()
        refreshUiState()
    }

    fun onProductClick(productId: Long) {
        viewModelScope.launch {
            _event.send(ProductListEvent.NavigateToDetailProduct(productId))
        }
    }

    fun onNavigateToCartClick() {
        viewModelScope.launch {
            _event.send(ProductListEvent.NavigateToShoppingCart)
        }
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                productPageStateHolder.updateItems(latestShoppingItems)
                refreshUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect {
                refreshUiState()
            }
        }
    }

    private fun refreshUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
        hasLoadedProducts: Boolean = _uiState.value.hasLoadedProducts,
    ) {
        val allShoppingItems = shoppingItemRepository.shoppingItems.value
        val recentViewedProductIds = visitStore.recentVisitedProductIds.value
        val shoppingItemByProductId =
            allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading,
                errorMessage = errorMessage,
                hasLoadedProducts = hasLoadedProducts,
                shoppingItems = productPageStateHolder.getItems(),
                recentViewedShoppingItems = recentViewedProductIds.mapNotNull { productId -> shoppingItemByProductId[productId] },
                shoppingCartTotalCount = allShoppingItems.sumOf { shoppingItem -> shoppingItem.getQuantity() },
                canLoadNextPage = productPageStateHolder.canMoveToNextPage(),
            )
        }
    }

    data class ProductListUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val hasLoadedProducts: Boolean = false,
        val shoppingItems: List<ShoppingItem> = emptyList(),
        val recentViewedShoppingItems: List<ShoppingItem> = emptyList(),
        val shoppingCartTotalCount: Int = 0,
        val canLoadNextPage: Boolean = false,
    )

    sealed interface ProductListEvent {
        data class NavigateToDetailProduct(
            val productId: Long,
        ) : ProductListEvent

        data object NavigateToShoppingCart : ProductListEvent
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
    }
}
