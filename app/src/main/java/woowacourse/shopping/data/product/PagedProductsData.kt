package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.entity.ProductEntity

class PagedProductsData(
    val products: List<ProductEntity>,
    val loadable: Boolean,
)
