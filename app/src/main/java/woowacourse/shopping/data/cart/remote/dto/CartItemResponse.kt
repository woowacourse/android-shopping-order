package woowacourse.shopping.data.cart.remote.dto

import woowacourse.shopping.data.common.dto.Pageable
import woowacourse.shopping.data.common.dto.Sort

data class CartItemResponse(
    val totalPages: Int,
    val totalElements: Long,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val pageable: Pageable,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartItemDto>,
    val empty: Boolean,
)
