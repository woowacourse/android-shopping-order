package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItemRequestBody(
    val productId: Int,
    val quantity: Int,
)
