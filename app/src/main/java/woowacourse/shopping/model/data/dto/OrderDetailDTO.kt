package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDetailDTO(
    @SerializedName("orderItems")
    val orderItems: List<OrderProductDTO>,
    @SerializedName("originalPrice")
    val originalPrice: Int,
    @SerializedName("usedPoints")
    val usedPoints: Int,
    @SerializedName("orderPrice")
    val orderPrice: Int
)
