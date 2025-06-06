package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS

data class CartProductUiState(
    val cartProducts: Products = EMPTY_PRODUCTS,
    val recommendedProducts: Products = EMPTY_PRODUCTS,
    val editedProductIds: Set<Long> = emptySet(),
    val isProductsLoading: Boolean = true,
    val connectionErrorMessage: String? = null,
)
