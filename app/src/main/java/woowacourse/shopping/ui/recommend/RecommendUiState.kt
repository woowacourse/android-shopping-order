package woowacourse.shopping.ui.recommend

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.ProductUiModel

data class RecommendUiState(
    val products: ImmutableList<ProductUiModel> = persistentListOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
