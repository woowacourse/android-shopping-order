package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.EMPTY_PRODUCT

data class ProductDetailUiState(
    val product: Product = EMPTY_PRODUCT,
    val lastHistoryProduct: HistoryProduct? = null,
    val isCartProductUpdateSuccess: Boolean? = null,
    val connectionErrorMessage: String? = null,
)
