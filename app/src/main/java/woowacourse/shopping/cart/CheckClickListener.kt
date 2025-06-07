package woowacourse.shopping.cart

import woowacourse.shopping.product.catalog.ProductUiModel

fun interface CheckClickListener {
    fun onClick(product: ProductUiModel)
}
