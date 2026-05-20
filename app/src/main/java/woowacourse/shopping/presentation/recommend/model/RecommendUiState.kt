package woowacourse.shopping.presentation.recommend.model

import woowacourse.shopping.presentation.productlist.model.ShoppingItemUiModel

data class RecommendUiState(
    val totalPrice: Long = 0L,
    val totalQuantity: Int = 0,
    val recommendProducts: List<ShoppingItemUiModel> = emptyList(),
)
