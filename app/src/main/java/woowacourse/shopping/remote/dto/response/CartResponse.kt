package woowacourse.shopping.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CartResponse(
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortDto,
    val first: Boolean,
    val last: Boolean,
    val pageable: PageableDto,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    @SerializedName("content") val cartDto: List<CartDto>,
    val empty: Boolean,
)

data class CartQuantityResponse(
    val quantity: Int,
)
