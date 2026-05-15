package woowacourse.shopping.ui.detail

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository
import woowacourse.shopping.data.local.datastore.VisitStore

class DetailProductViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
) : ViewModel() {
    /**
     * DetailProductUiState는 무엇이라고 생각하시나요?
     * cachedShoppingItems는 UiState에서 저장하고 있는 값인데, 다른 변수에 따로 저장하는 이유는 무엇일까요?
     */
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

    /**
     * Bundle 자체를 ViewModel로 넘기게 된 이유는 무엇이었을까요?
     * 상품의 id를 넘겨받아 작업하는 것과는 어떻게 다른가요?
     * 그리고 요청을 하기 위해 상품의 id를 꺼내는 작업은 Activity에서 하고 있기도 하네요.
     */
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
        if (selectedProductId != productId) {
            selectedProductId = productId
            selectedQuantity = DEFAULT_QUANTITY
        }
        viewModelScope.launch {
            visitStore.visit(productId)
        }
        publishUiState()
    }

    fun increaseSelectedQuantity() {
        selectedQuantity += 1
        publishUiState()
    }

    /**
     * 함수명에 관한 이야기인데요.
     * increaseQuantity와 같이 사용한다면 더 간단하게 같은 의미를 전달해줄 수 있지 않을까요?
     */
    fun decreaseSelectedQuantity() {
        if (selectedQuantity <= DEFAULT_QUANTITY) {
            return
        }
        selectedQuantity -= 1
        publishUiState()
    }

    fun addSelectedProductToCart() {
        val shoppingItem = uiState.value.shoppingItem ?: return
        viewModelScope.launch {
            addSelectedProductQuantityToCart(
                productId = shoppingItem.getProductId(),
                quantity = selectedQuantity,
            )
        }
    }

    private suspend fun addSelectedProductQuantityToCart(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity < 1) {
            return
        }
        shoppingCartRepository.addIfAbsent(productId)
        shoppingItemRepository.plusQuantity(productId, quantity)
    }

    private fun publishUiState() {
        _uiState.value = createUiState()
    }

    private fun createUiState(): DetailProductUiState {
        val shoppingItemByProductId = cachedShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val currentShoppingItem = selectedProductId?.let { productId -> shoppingItemByProductId[productId] }
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
        const val EXTRA_PRODUCT_ID = "productId"
        const val EXTRA_SHOW_LAST_VIEWED = "showLastViewed"
        private const val DEFAULT_QUANTITY = 1
        private const val INVALID_PRODUCT_ID = -1L
    }
}
