package woowacourse.shopping.ui.recommendation

import woowacourse.shopping.ui.uimodel.CartProductUiModel
import woowacourse.shopping.ui.uimodel.ProductUiModel

data class RecommendationUiState(
    val recommendedProducts: List<ProductUiModel> = emptyList(),
    val cartItems: List<CartProductUiModel> = emptyList(),
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
) {
    fun isContainedInCart(productId: Long): Boolean = cartItems.any { it.productId == productId }

    fun productCount(productId: Long): Int = cartItems.firstOrNull { it.productId == productId }?.quantity ?: 0
}
