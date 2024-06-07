package woowacourse.shopping.data.remote.dto.cart

import com.google.gson.annotations.SerializedName

data class CartItemRequest(
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("quantity")
    val quantity: Int,
)
