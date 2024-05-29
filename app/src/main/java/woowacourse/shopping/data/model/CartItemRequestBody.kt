package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItemRequestBody(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
)
