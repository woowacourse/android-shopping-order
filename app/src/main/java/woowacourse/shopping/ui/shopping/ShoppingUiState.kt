package woowacourse.shopping.ui.shopping

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.ProductUiModel

data class ShoppingUiState(
    val products: ImmutableList<ProductUiModel> = persistentListOf(),
    val recentItems: ImmutableList<ProductUiModel> = persistentListOf(),
    val uiInfo: UiInfoState = UiInfoState(),
    val cartSummary: CartSummaryState = CartSummaryState(),
)

data class UiInfoState(
    val isLoading: Boolean = false,
    val cartErrorMessage: String? = null,
    val isNetworkAvailable: Boolean = true,
)

data class CartSummaryState(
    val cartQuantities: Map<String, Int> = emptyMap(),
    val cartSize: Int = 0,
    val canLoadMore: Boolean = true,
)
