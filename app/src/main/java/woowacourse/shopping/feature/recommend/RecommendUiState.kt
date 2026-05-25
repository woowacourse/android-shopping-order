package woowacourse.shopping.feature.recommend

import woowacourse.shopping.feature.common.state.ProductUiModel

data class RecommendUiState(
    val recommendList: List<ProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
)
