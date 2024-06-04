package woowacourse.shopping.data.datasource.remote.model.response.product

import woowacourse.shopping.data.datasource.remote.model.response.Sort

data class ProductResponse(
    val productContent: List<ProductContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)
