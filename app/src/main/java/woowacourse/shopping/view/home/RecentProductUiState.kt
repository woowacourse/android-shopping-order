package woowacourse.shopping.view.home

import woowacourse.shopping.domain.model.RecentProduct

data class RecentProductUiState(
    val isLoading: Boolean = true,
    val isEmpty: Boolean = true,
    val productItems: List<RecentProduct> = emptyList(),
)
