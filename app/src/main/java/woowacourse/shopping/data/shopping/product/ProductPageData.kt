package woowacourse.shopping.data.shopping.product

import woowacourse.shopping.domain.entity.Product

data class ProductPageData(
    val pageNumber: Int,
    val products: List<Product>,
    val totalPageSize: Int,
    val pageSize: Int,
    val totalProductSize: Int,
)
