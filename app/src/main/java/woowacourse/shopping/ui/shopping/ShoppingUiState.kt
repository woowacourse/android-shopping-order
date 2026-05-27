package woowacourse.shopping.ui.shopping

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.model.RecentUiModel

data class ShoppingUiState(
    val products: ImmutableList<ProductUiModel> = persistentListOf(),
    val recentItems: ImmutableList<RecentUiModel> = persistentListOf(),
    val cartSize: Int = 0,
    val cartQuantities: Map<String, Int> = emptyMap(),
    val canLoadMore: Boolean = true,
    val isLoading: Boolean = false,
    val isPagingMore: Boolean = false,
    val isNetworkAvailable: Boolean = true,
    val cartErrorMessage: String? = null,
)
