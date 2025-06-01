package woowacourse.shopping.data.carts.dto

import com.google.gson.annotations.SerializedName

data class CartItemRequest(
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int,
)
