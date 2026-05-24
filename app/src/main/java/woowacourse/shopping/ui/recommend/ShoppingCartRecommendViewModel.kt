package woowacourse.shopping.ui.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ShoppingCartRecommendViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingCartRecommendUiState())
    val uiState: StateFlow<ShoppingCartRecommendUiState> = _uiState.asStateFlow()

    private var shoppingCartItems: List<ShoppingCartItem> = emptyList()
    private var selectedCartProductIds: Set<Long> = emptySet()
    private var recommendBaseCartProductIds: Set<Long> = emptySet()
    private var recommendBaseSelectedCartItemCount: Int = 0
    private var recommendBaseSelectedCartTotalPrice: Int = 0

    init {
        observeSources()
        publishUiState()
    }

    fun updateCartSnapshot(
        shoppingCartItems: List<ShoppingCartItem>,
        selectedCartProductIds: Set<Long>,
    ) {
        if (
            this.shoppingCartItems == shoppingCartItems &&
            this.selectedCartProductIds == selectedCartProductIds
        ) {
            return
        }
        this.shoppingCartItems = shoppingCartItems
        this.selectedCartProductIds = selectedCartProductIds
        publishUiState()
    }

    fun moveToRecommend(
        baseCartItems: List<ShoppingCartItem> = shoppingCartItems,
        baseSelectedCartProductIds: Set<Long> = selectedCartProductIds,
    ) {
        recommendBaseCartProductIds =
            baseCartItems
                .map { shoppingCartItem -> shoppingCartItem.product.id }
                .toSet()
        recommendBaseSelectedCartItemCount = baseSelectedCartProductIds.size
        recommendBaseSelectedCartTotalPrice =
            baseCartItems
                .filter { shoppingCartItem -> shoppingCartItem.product.id in baseSelectedCartProductIds }
                .sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
        publishUiState(currentStep = ShoppingCartStep.RECOMMEND)
    }

    fun moveToCart() {
        recommendBaseCartProductIds = emptySet()
        recommendBaseSelectedCartItemCount = 0
        recommendBaseSelectedCartTotalPrice = 0
        publishUiState(currentStep = ShoppingCartStep.CART)
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect {
                publishUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect {
                publishUiState()
            }
        }
    }

    private fun publishUiState(currentStep: ShoppingCartStep = _uiState.value.currentStep) {
        _uiState.value = createUiState(currentStep = currentStep)
    }

    private fun createUiState(currentStep: ShoppingCartStep): ShoppingCartRecommendUiState {
        val allShoppingItems = shoppingItemRepository.shoppingItems.value
        val recentViewedProductIds = visitStore.recentVisitedProductIds.value
        val shoppingItemByProductId =
            allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val mostRecentViewedProductId = recentViewedProductIds.firstOrNull()
        val mostRecentViewedCategory =
            mostRecentViewedProductId
                ?.let { productId -> shoppingItemByProductId[productId] }
                ?.getProduct()
                ?.category
        val cartProductIds =
            shoppingCartItems
                .map { shoppingCartItem -> shoppingCartItem.product.id }
                .toSet()
        val excludedProductIdsForRecommend =
            if (currentStep == ShoppingCartStep.RECOMMEND) {
                recommendBaseCartProductIds
            } else {
                cartProductIds
            }
        val recommendedShoppingItems =
            if (mostRecentViewedCategory == null || mostRecentViewedCategory == UNKNOWN_CATEGORY) {
                emptyList()
            } else {
                allShoppingItems
                    .filter { shoppingItem ->
                        shoppingItem.getProduct().category == mostRecentViewedCategory &&
                            shoppingItem.getProductId() !in excludedProductIdsForRecommend
                    }.take(MAX_RECOMMEND_PRODUCTS)
            }
        val dynamicSelectedCartTotalPrice =
            shoppingCartItems
                .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedCartProductIds }
                .sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
        val selectedCartTotalPrice =
            if (currentStep == ShoppingCartStep.RECOMMEND) {
                recommendBaseSelectedCartTotalPrice
            } else {
                dynamicSelectedCartTotalPrice
            }
        val baseSelectedCartItemCount =
            if (currentStep == ShoppingCartStep.RECOMMEND) {
                recommendBaseSelectedCartItemCount
            } else {
                selectedCartProductIds.size
            }
        val selectedRecommendTotalPrice =
            recommendedShoppingItems
                .filter { shoppingItem -> shoppingItem.getQuantity() > 0 }
                .sumOf { shoppingItem -> shoppingItem.getProductQuantityPrice() }

        return ShoppingCartRecommendUiState(
            currentStep = currentStep,
            recommendedShoppingItems = recommendedShoppingItems,
            baseSelectedCartItemCount = baseSelectedCartItemCount,
            selectedCartTotalPrice = selectedCartTotalPrice,
            selectedRecommendTotalPrice = selectedRecommendTotalPrice,
        )
    }

    data class ShoppingCartRecommendUiState(
        val currentStep: ShoppingCartStep = ShoppingCartStep.CART,
        val recommendedShoppingItems: List<ShoppingItem> = emptyList(),
        val baseSelectedCartItemCount: Int = 0,
        val selectedCartTotalPrice: Int = 0,
        val selectedRecommendTotalPrice: Int = 0,
    )

    enum class ShoppingCartStep {
        CART,
        RECOMMEND,
    }

    private companion object {
        private const val MAX_RECOMMEND_PRODUCTS = 10
        private const val UNKNOWN_CATEGORY = "UNKNOWN"
    }
}
