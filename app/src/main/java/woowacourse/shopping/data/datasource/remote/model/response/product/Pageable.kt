package woowacourse.shopping.data.datasource.remote.model.response.product

import woowacourse.shopping.data.datasource.remote.model.response.Sort

data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean,
)
