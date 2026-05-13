package woowacourse.shopping.data.source.remote.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.source.remote.dto.Pageable
import woowacourse.shopping.data.source.remote.dto.Sort

@Serializable
data class CartResponse(
    @SerialName("content")
    val cartContent: List<CartContent>,
    @SerialName("empty")
    val empty: Boolean,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("number")
    val number: Int,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("pageable")
    val pageable: Pageable,
    @SerialName("size")
    val size: Int,
    @SerialName("sort")
    val sort: Sort,
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int,
)
