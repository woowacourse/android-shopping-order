package woowacourse.shopping.domain.repository.query

import woowacourse.shopping.domain.model.product.Product

data class ProductPageResult(
    val items: List<Product>,
    val totalElements: Int,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
)
