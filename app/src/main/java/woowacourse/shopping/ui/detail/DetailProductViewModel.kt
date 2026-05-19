package woowacourse.shopping.ui.detail

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository

class DetailProductViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val productRetrofitRepository: ProductRetrofitRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState: StateFlow<DetailProductUiState> = _uiState.asStateFlow()

    private val detailRequestMutex = Mutex()
    private var requestedProductIds: Set<Long> = emptySet()
    private var cachedShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value
    private var selectedProductId: Long? = null
    private var selectedQuantity: Int = DEFAULT_QUANTITY
    private var showLastViewedSection: Boolean = true
    private var lastViewedProductIdForCurrentDetail: Long? = null

    init {
        publishUiState()
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                cachedShoppingItems = latestShoppingItems
                publishUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect { latestRecentViewedProductIds ->
                recentViewedProductIds = latestRecentViewedProductIds
                publishUiState()
            }
        }
    }

    fun initializeFromIntentExtras(extras: Bundle?) {
        val productId = extras?.getLong(EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID) ?: INVALID_PRODUCT_ID
        if (productId == INVALID_PRODUCT_ID) {
            return
        }
        val showLastViewed = extras?.getBoolean(EXTRA_SHOW_LAST_VIEWED, true) ?: true
        initialize(
            productId = productId,
            showLastViewed = showLastViewed,
        )
    }

    private fun initialize(
        productId: Long,
        showLastViewed: Boolean = true,
    ) {
        showLastViewedSection = showLastViewed
        val recentProductIdsBeforeVisit = visitStore.recentVisitedProductIds.value
        if (selectedProductId != productId) {
            selectedProductId = productId
            selectedQuantity = DEFAULT_QUANTITY
        }
        lastViewedProductIdForCurrentDetail =
            resolveLastViewedProductId(
                recentProductIds = recentProductIdsBeforeVisit,
            )
        viewModelScope.launch {
            visitStore.visit(productId)
        }
        requestProductDetailIfNeeded(productId)
        publishUiState()
    }

    fun increaseSelectedQuantity() {
        selectedQuantity += 1
        publishUiState()
    }

    fun decreaseSelectedQuantity() {
        if (selectedQuantity <= DEFAULT_QUANTITY) {
            return
        }
        selectedQuantity -= 1
        publishUiState()
    }

    private fun requestProductDetailIfNeeded(productId: Long) {
        viewModelScope.launch {
            detailRequestMutex.withLock {
                if (requestedProductIds.contains(productId)) return@withLock
                requestedProductIds = requestedProductIds + productId

                runCatching {
                    productRetrofitRepository
                        .requestProductDetail(
                            id = productId,
                        )
                        .toDomainProduct()
                }.onSuccess { detailProduct ->
                    shoppingItemRepository.upsertProduct(detailProduct)
                }.onFailure {
                    requestedProductIds = requestedProductIds - productId
                }
            }
        }
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): DetailProductUiState {
        val shoppingItemByProductId = cachedShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val currentShoppingItem = selectedProductId?.let { productId -> shoppingItemByProductId[productId] }
        val safeQuantity = selectedQuantity.coerceAtLeast(DEFAULT_QUANTITY)
        val resolvedLastViewedShoppingItem =
            lastViewedProductIdForCurrentDetail
                ?.takeIf { showLastViewedSection && it != selectedProductId }
                ?.let { productId -> shoppingItemByProductId[productId] }
        return DetailProductUiState(
            shoppingItem = currentShoppingItem,
            lastViewedShoppingItem = resolvedLastViewedShoppingItem,
            selectedQuantity = safeQuantity,
            quantityPrice = currentShoppingItem?.getProductQuantityPrice(safeQuantity) ?: 0,
        )
    }

    private fun resolveLastViewedProductId(
        recentProductIds: List<Long>,
    ): Long? = recentProductIds.firstOrNull()

    data class DetailProductUiState(
        val shoppingItem: ShoppingItem? = null,
        val lastViewedShoppingItem: ShoppingItem? = null,
        val selectedQuantity: Int = DEFAULT_QUANTITY,
        val quantityPrice: Int = 0,
    )

    companion object {
        const val EXTRA_PRODUCT_ID = "productId"
        const val EXTRA_SHOW_LAST_VIEWED = "showLastViewed"
        private const val DEFAULT_QUANTITY = 1
        private const val INVALID_PRODUCT_ID = -1L
    }
}
