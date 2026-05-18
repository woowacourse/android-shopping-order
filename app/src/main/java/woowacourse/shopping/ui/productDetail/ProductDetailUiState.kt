package woowacourse.shopping.ui.productDetail

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.ui.util.LoadState

data class ProductDetailUiState(
    val product: Product? = null,
    val currentQuantity: Int = 1,
    val lastViewedProduct: Product? = null,
    val loadState: LoadState = LoadState.Initial,
)
