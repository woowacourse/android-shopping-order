package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore

class ShoppingCartRecommendViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingCartRecommendUiState())
    val uiState: StateFlow<ShoppingCartRecommendUiState> = _uiState.asStateFlow()

    private var allShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value
    private var shoppingCartItems: List<ShoppingCartItem> = emptyList()
    private var selectedCartProductIds: Set<Long> = emptySet()
    private var recommendBaseCartProductIds: Set<Long> = emptySet()

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

    fun moveToRecommend() {
        recommendBaseCartProductIds =
            shoppingCartItems
                .map { shoppingCartItem -> shoppingCartItem.product.id }
                .toSet()
        publishUiState(currentStep = ShoppingCartStep.RECOMMENT)
    }

    fun moveToCart() {
        recommendBaseCartProductIds = emptySet()
        publishUiState(currentStep = ShoppingCartStep.CART)
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                allShoppingItems = latestShoppingItems
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

    private fun publishUiState(currentStep: ShoppingCartStep = _uiState.value.currentStep) {
        _uiState.value = createUiState(currentStep = currentStep)
    }

    private fun createUiState(currentStep: ShoppingCartStep): ShoppingCartRecommendUiState {
        val shoppingItemByProductId =
            allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        val mostRecentViewedCategory =
            recentViewedProductIds.firstNotNullOfOrNull { productId ->
                shoppingItemByProductId[productId]?.getProduct()?.category
            }
        val cartProductIds =
            shoppingCartItems
                .map { shoppingCartItem -> shoppingCartItem.product.id }
                .toSet()
        val excludedProductIdsForRecommend =
            if (currentStep == ShoppingCartStep.RECOMMENT) {
                recommendBaseCartProductIds
            } else {
                cartProductIds
            }
        val recommendedShoppingItems =
            if (mostRecentViewedCategory == null) {
                emptyList()
            } else {
                allShoppingItems
                    .filter { shoppingItem ->
                        shoppingItem.getProduct().category == mostRecentViewedCategory &&
                            shoppingItem.getProductId() !in excludedProductIdsForRecommend
                    }.take(MAX_RECOMMEND_PRODUCTS)
            }
        val selectedCartTotalPrice =
            shoppingCartItems
                .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedCartProductIds }
                .sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
        val selectedRecommendTotalPrice =
            recommendedShoppingItems
                .filter { shoppingItem -> shoppingItem.getQuantity() > 0 }
                .sumOf { shoppingItem -> shoppingItem.getProductQuantityPrice() }

        return ShoppingCartRecommendUiState(
            currentStep = currentStep,
            recommendedShoppingItems = recommendedShoppingItems,
            selectedCartTotalPrice = selectedCartTotalPrice,
            selectedRecommendTotalPrice = selectedRecommendTotalPrice,
        )
    }

    data class ShoppingCartRecommendUiState(
        val currentStep: ShoppingCartStep = ShoppingCartStep.CART,
        val recommendedShoppingItems: List<ShoppingItem> = emptyList(),
        val selectedCartTotalPrice: Int = 0,
        val selectedRecommendTotalPrice: Int = 0,
    )

    enum class ShoppingCartStep {
        CART,
        RECOMMENT,
    }

    private companion object {
        private const val MAX_RECOMMEND_PRODUCTS = 10
    }
}
