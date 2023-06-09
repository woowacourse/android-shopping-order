package woowacourse.shopping.data.dto.response

import com.example.domain.model.OrderDetail
import com.google.gson.annotations.SerializedName

data class OrderDetailDto(
    @SerializedName("priceBeforeDiscount")
    val priceBeforeDiscount: Int,

    @SerializedName("priceAfterDiscount")
    val priceAfterDiscount: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("orderItems")
    val orderItems: List<OrderProductDto>,
) {
    fun toDomain() = OrderDetail(
        priceBeforeDiscount,
        priceAfterDiscount,
        date,
        orderItems.map { it.toDomain() },
    )
}
