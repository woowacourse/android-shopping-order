package woowacourse.shopping.data.model.dto.response

import com.example.domain.model.OrderMinInfoItem
import com.example.domain.model.Price
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderMinInfoItemDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("mainProductName")
    val mainProductName: String,
    @SerializedName("mainProductImage")
    val mainProductImage: String,
    @SerializedName("extraProductCount")
    val extraProductCount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("price")
    val price: Int
) {
    fun toDomain(): OrderMinInfoItem {
        val localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return OrderMinInfoItem(
            id, mainProductName, mainProductImage, extraProductCount, localDateTime,
            Price(price)
        )
    }
}
