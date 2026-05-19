package woowacourse.shopping.data.source.remote.dto.cart.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.source.remote.dto.common.Pageable
import woowacourse.shopping.data.source.remote.dto.common.Sort

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

@Serializable
data class CartContent(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val productContent: CartProductContent,
    @SerialName("quantity")
    val quantity: Int,
)

@Serializable
data class CartProductContent(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
)
