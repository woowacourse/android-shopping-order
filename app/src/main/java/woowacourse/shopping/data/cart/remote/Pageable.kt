package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.data.product.remote.retrofit.Sort

data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean,
)
