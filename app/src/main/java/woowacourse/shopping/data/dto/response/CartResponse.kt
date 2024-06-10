package woowacourse.shopping.data.dto.response

import com.google.gson.annotations.SerializedName

data class CartResponse(
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortResponse,
    val first: Boolean,
    val last: Boolean,
    val pageable: PageableResponse,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    @SerializedName("content") val cartItemResponse: List<CartItemResponse>,
    val empty: Boolean,
)

data class CartQuantityResponse(
    val quantity: Int,
)
