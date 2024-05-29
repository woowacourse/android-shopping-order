package woowacourse.shopping.data.remote.dto.cart

import com.google.gson.annotations.SerializedName

data class CartItemQuantityDto(
    @SerializedName("quantity")
    val quantity: Int,
)
