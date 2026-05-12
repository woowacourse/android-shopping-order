package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore

class DetailProductViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
    private val visitStore: VisitStore = ShoppingApplication.visitStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState: StateFlow<DetailProductUiState> = _uiState.asStateFlow()

    private var cachedShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var selectedProductId: Long? = null
    private var selectedQuantity: Int = DEFAULT_QUANTITY
    private var showLastViewedSection: Boolean = true
    private var lastViewedProductId: Long? = null

    init {
        updateUiState()
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                cachedShoppingItems = latestShoppingItems
                updateUiState()
            }
        }
    }

    fun initialize(
        productId: Long,
        showLastViewed: Boolean = true,
    ) {
        showLastViewedSection = showLastViewed
        val recentProductIds = visitStore.recentVisitedProductIds.value
        lastViewedProductId =
            if (showLastViewed) {
                resolveLastViewedProductId(
                    productId = productId,
                    recentProductIds = recentProductIds,
                )
            } else {
                null
            }
        if (selectedProductId != productId) {
            selectedProductId = productId
            selectedQuantity = DEFAULT_QUANTITY
        }
        visitStore.visit(productId)
        updateUiState()
    }

    fun increaseSelectedQuantity() {
        selectedQuantity += 1
        updateUiState()
    }

    fun decreaseSelectedQuantity() {
        if (selectedQuantity <= DEFAULT_QUANTITY) {
            return
        }
        selectedQuantity -= 1
        updateUiState()
    }

    fun addSelectedProductToCart() {
        val shoppingItem = uiState.value.shoppingItem ?: return
        addProductToCart(
            shoppingItem = shoppingItem,
            selectedQuantity = selectedQuantity,
        )
    }

    fun addProductToCart(
        shoppingItem: ShoppingItem,
        selectedQuantity: Int,
    ) {
        launchNow {
            if (selectedQuantity < 1) {
                return@launchNow
            }
            val productId = shoppingItem.getProductId()
            val sourceItem = shoppingItemRepository.getShoppingItemOrNull(productId) ?: return@launchNow
            shoppingCartRepository.add(sourceItem)
            shoppingItemRepository.plusQuantity(productId, selectedQuantity)
        }
    }

    private fun updateUiState() {
        val shoppingItemByProductId = cachedShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val currentShoppingItem = selectedProductId?.let { productId -> shoppingItemByProductId[productId] }
        val safeQuantity = selectedQuantity.coerceAtLeast(DEFAULT_QUANTITY)
        val resolvedLastViewedShoppingItem =
            lastViewedProductId
                ?.takeIf { showLastViewedSection && it != selectedProductId }
                ?.let { productId -> shoppingItemByProductId[productId] }
        _uiState.value =
            DetailProductUiState(
                shoppingItem = currentShoppingItem,
                lastViewedShoppingItem = resolvedLastViewedShoppingItem,
                selectedQuantity = safeQuantity,
                quantityPrice = currentShoppingItem?.getProductQuantityPrice(safeQuantity) ?: 0,
            )
    }

    private fun launchNow(block: suspend () -> Unit) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            block()
        }
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
