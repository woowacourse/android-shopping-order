package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class OrderPayDTO(
    @SerializedName("cartItemIds")
    val cartItemIds: List<CartItemIdDTO>,
    @SerializedName("originalPrice")
    val originalPrice: Int,
    @SerializedName("points")
    val points: Int
)
