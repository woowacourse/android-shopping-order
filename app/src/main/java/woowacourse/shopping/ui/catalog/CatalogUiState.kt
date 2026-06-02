package woowacourse.shopping.ui.catalog

import woowacourse.shopping.ui.uimodel.CartProductUiModel
import woowacourse.shopping.ui.uimodel.ProductUiModel

data class CatalogUiState(
    val products: List<ProductUiModel> = emptyList(),
    val recentlyViewedProducts: List<ProductUiModel> = emptyList(),
    val cartItems: List<CartProductUiModel> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
) {
    fun isContainedInCart(productId: Long): Boolean = cartItems.any { it.productId == productId }

    fun productCount(productId: Long): Int = cartItems.firstOrNull { it.productId == productId }?.quantity ?: 0
}
