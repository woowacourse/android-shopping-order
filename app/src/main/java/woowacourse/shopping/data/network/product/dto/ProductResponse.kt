package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.network.cart.dto.Pageable
import woowacourse.shopping.data.network.cart.dto.Sort

@Serializable
data class ProductResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int,
)
