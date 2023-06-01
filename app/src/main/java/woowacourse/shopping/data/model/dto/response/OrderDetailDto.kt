package woowacourse.shopping.data.model.dto.response

import com.example.domain.model.OrderDetail
import com.example.domain.model.Price
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderDetailDto(
    @SerializedName("priceBeforeDiscount")
    val priceBeforeDiscount: Int,
    @SerializedName("priceAfterDiscount")
    val priceAfterDiscount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("orderItems")
    val orderItems: List<OrderProductDto>
) {
    fun toDomain(): OrderDetail {
        val localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return OrderDetail(
            Price(priceBeforeDiscount),
            Price(priceAfterDiscount),
            localDateTime,
            orderItems.map { it.toDomain() }
        )
    }
}
