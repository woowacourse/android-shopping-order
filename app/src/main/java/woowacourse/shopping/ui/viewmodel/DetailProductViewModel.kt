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
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore
import kotlin.uuid.ExperimentalUuidApi

sealed interface DetailProductEvent {
    data object AddToCartSuccess : DetailProductEvent
    data class AddToCartFailure(val message: String) : DetailProductEvent
}

@OptIn(ExperimentalUuidApi::class)
class DetailProductViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
) : ViewModel() {
    private val _event = MutableSharedFlow<DetailProductEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<DetailProductEvent> = _event.asSharedFlow()
    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState: StateFlow<DetailProductUiState> = _uiState.asStateFlow()

    private var cachedShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value
    private var selectedProductId: Long? = null
    private var selectedQuantity: Int = DEFAULT_QUANTITY
    private var showLastViewedSection: Boolean = true

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

    fun initialize(
        productId: Long,
        showLastViewed: Boolean = true,
    ) {
        showLastViewedSection = showLastViewed
        if (selectedProductId != productId) {
            selectedProductId = productId
            selectedQuantity = DEFAULT_QUANTITY
        }
        viewModelScope.launch {
            visitStore.visit(productId)
        }
        publishUiState()
    }


    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            shoppingItemRepository.fetchProductById(productId)
            publishUiState()
        }
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

    fun addSelectedProductToCart() {
        val productId = selectedProductId ?: return
        val quantity = selectedQuantity
        if (quantity < 1) return

        viewModelScope.launch {
            runCatching {
                shoppingCartRepository.addIfAbsent(productId)
                shoppingItemRepository.plusQuantity(productId, quantity)
            }.onSuccess {
                _event.tryEmit(DetailProductEvent.AddToCartSuccess)
            }.onFailure { throwable ->
                _event.tryEmit(
                    DetailProductEvent.AddToCartFailure(
                        throwable.message ?: "장바구니 담기 실패",
                    ),
                )
            }
        }
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): DetailProductUiState {
        val shoppingItemByProductId =
            cachedShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val currentShoppingItem =
            selectedProductId?.let { productId -> shoppingItemByProductId[productId] }
        val safeQuantity = selectedQuantity.coerceAtLeast(DEFAULT_QUANTITY)
        val lastViewedProductId =
            selectedProductId?.let { productId ->
                resolveLastViewedProductId(
                    productId = productId,
                    recentProductIds = recentViewedProductIds,
                )
            }
        val resolvedLastViewedShoppingItem =
            lastViewedProductId
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
        productId: Long,
        recentProductIds: List<Long>,
    ): Long? {
        if (recentProductIds.isEmpty()) {
            return null
        }
        val currentProductIndex = recentProductIds.indexOf(productId)
        return when {
            currentProductIndex == 0 -> null
            currentProductIndex > 0 -> recentProductIds[currentProductIndex - 1]
            else -> recentProductIds.firstOrNull()
        }
    }

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
