package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.productList.ProductUiModel
import woowacourse.shopping.ui.util.LoadState

data class RecommendUiState(
    val recommendProducts: List<ProductUiModel> = emptyList(),
    val loadState: LoadState = LoadState.Initial,
)
