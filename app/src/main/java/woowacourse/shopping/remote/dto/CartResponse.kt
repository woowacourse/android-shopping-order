package woowacourse.shopping.remote.dto

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("content")
    val cartItems: List<CartItemDto>,
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageableDto: PageableDto,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sortDto: SortDto,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)
