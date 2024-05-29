package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("content")
    val cartItems: List<CartItem2>,
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
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)

data class CartItem2(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product2,
)
