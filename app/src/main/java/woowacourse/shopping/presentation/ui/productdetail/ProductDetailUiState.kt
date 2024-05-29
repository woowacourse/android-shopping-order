package woowacourse.shopping.presentation.ui.productdetail

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

data class ProductDetailUiState(
    val cart: Cart? = null,
    val productHistory: Product? = null,
    val isLastProductPage: Boolean = false,
)
