package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.entity.ProductEntity

class PageableProducts(
    val products: List<ProductEntity>,
    val hasNext: Boolean,
)
