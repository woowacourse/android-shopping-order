package woowacourse.shopping.data.carts.dto

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("content")
    val content: List<CartContent>,
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
    val pageable: PageableX,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: SortXX,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)
