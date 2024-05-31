package woowacourse.shopping.remote.product

import woowacourse.shopping.remote.common.Pageable
import woowacourse.shopping.remote.common.Sort

data class ProductResponse(
    val content: List<ProductDto>,
    val pageable: Pageable,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Long,
    val sort: Sort,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
