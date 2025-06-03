package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

data class OrderInfo(
    val checkedItems: List<ProductUiModel>,
)
