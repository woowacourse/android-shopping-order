package woowacourse.shopping.data.model

import woowacourse.shopping.product.catalog.model.Pageable
import woowacourse.shopping.product.catalog.model.Product
import woowacourse.shopping.product.catalog.model.Sort

data class ProductResponse(
    val content: List<Product>,
    val pageable: Pageable,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sort: Sort,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean
)