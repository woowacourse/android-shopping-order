package woowacourse.shopping.repository.query

import woowacourse.shopping.model.Product

data class ProductPageResult(
    val items: List<Product>,
    val totalElements: Int,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
)
