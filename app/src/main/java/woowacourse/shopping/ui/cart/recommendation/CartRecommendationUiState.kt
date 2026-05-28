package woowacourse.shopping.ui.cart.recommendation

import woowacourse.shopping.ui.navigation.OrderProduct
import woowacourse.shopping.ui.shopping.ShoppingProductUiState

data class CartRecommendationUiState(
    val recommendedProducts: List<ShoppingProductUiState> = emptyList(),
    val isRecommendedProductsLoading: Boolean = false,
    val pendingOrder: PendingOrderUiState = PendingOrderUiState(),
    val isApplying: Boolean = false,
    val orderProductsToOrder: List<OrderProduct>? = null,
    val errorMessage: String? = null,
    val isNetworkConnected: Boolean = true,
)

data class PendingOrderUiState(
    val cartItemIds: List<Long> = emptyList(),
    val excludedProductIds: Set<Long> = emptySet(),
    val selectedCount: Int = 0,
    val totalPrice: Int = 0,
)
