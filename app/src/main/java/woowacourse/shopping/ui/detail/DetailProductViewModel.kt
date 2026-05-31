package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class DetailProductViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState: StateFlow<DetailProductUiState> = _uiState.asStateFlow()

    private val requestedProductIds: MutableSet<Long> = mutableSetOf()
    private var selectedProductId: Long? = null
    private var lastViewedProductIdForCurrentDetail: Long? = null

    init {
        publishUiState()
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect {
                publishUiState()
            }
        }
    }

    fun initialize(
        productId: Long,
        showLastViewed: Boolean = true,
    ) {
        val recentProductIdsBeforeVisit = visitStore.recentVisitedProductIds.value
        if (selectedProductId != productId) {
            selectedProductId = productId
            _uiState.update { currentState ->
                currentState.copy(selectedQuantity = DEFAULT_QUANTITY)
            }
        }
        lastViewedProductIdForCurrentDetail =
            if (showLastViewed) {
                resolveLastViewedProductId(
                    recentProductIds = recentProductIdsBeforeVisit,
                )
            } else {
                null
            }
        viewModelScope.launch {
            visitStore.visit(productId)
        }
        requestProductDetailIfNeeded(productId)
        publishUiState()
    }

    fun increaseSelectedQuantity() {
        _uiState.update { currentState ->
            currentState.copy(selectedQuantity = currentState.selectedQuantity + 1)
        }
        publishUiState()
    }

    fun decreaseSelectedQuantity() {
        if (_uiState.value.selectedQuantity <= DEFAULT_QUANTITY) {
            return
        }
        _uiState.update { currentState ->
            currentState.copy(selectedQuantity = currentState.selectedQuantity - 1)
        }
        publishUiState()
    }

    private fun requestProductDetailIfNeeded(productId: Long) {
        viewModelScope.launch {
            if (!requestedProductIds.add(productId)) return@launch

            try {
                val detailProduct =
                    productRepository.requestProductDetail(
                        id = productId,
                    )
                shoppingItemRepository.upsertProducts(listOf(detailProduct))
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                requestedProductIds.remove(productId)
            }
        }
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): DetailProductUiState {
        val shoppingItemByProductId =
            shoppingItemRepository.shoppingItems.value
                .associateBy { shoppingItem -> shoppingItem.getProductId() }
        val currentShoppingItem = selectedProductId?.let { productId -> shoppingItemByProductId[productId] }
        val safeQuantity = _uiState.value.selectedQuantity.coerceAtLeast(DEFAULT_QUANTITY)
        val resolvedLastViewedShoppingItem =
            lastViewedProductIdForCurrentDetail
                ?.takeIf { it != selectedProductId }
                ?.let { productId -> shoppingItemByProductId[productId] }
        return DetailProductUiState(
            shoppingItem = currentShoppingItem,
            lastViewedShoppingItem = resolvedLastViewedShoppingItem,
            selectedQuantity = safeQuantity,
            quantityPrice = currentShoppingItem?.getProductQuantityPrice(safeQuantity) ?: 0,
        )
    }

    private fun resolveLastViewedProductId(recentProductIds: List<Long>): Long? = recentProductIds.firstOrNull()

    data class DetailProductUiState(
        val shoppingItem: ShoppingItem? = null,
        val lastViewedShoppingItem: ShoppingItem? = null,
        val selectedQuantity: Int = DEFAULT_QUANTITY,
        val quantityPrice: Int = 0,
    )

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
