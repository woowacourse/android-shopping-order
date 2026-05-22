package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.Product

data class ProductDetailUIState(
    val product: Product? = null,
    val lastViewProduct: Product? = null,
    val count: Int = 1,
    val errorMsg: String? = null,
)
