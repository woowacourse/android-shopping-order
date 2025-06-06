package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS

data class CatalogUiState(
    val catalogProducts: Products = EMPTY_PRODUCTS,
    val historyProducts: List<HistoryProduct> = emptyList(),
    val cartProductsQuantity: Int = INITIAL_PRODUCT_QUANTITY,
    val isProductsLoading: Boolean = true,
    val connectionErrorMessage: String? = null,
) {
    companion object {
        private const val INITIAL_PRODUCT_QUANTITY: Int = 0
    }
}
