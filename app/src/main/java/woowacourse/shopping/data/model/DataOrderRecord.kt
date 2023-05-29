package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DataOrderRecord(
    val orderId: Long,
    @SerializedName("createdAt")
    val orderedTime: Date?,
    @SerializedName("orderItems")
    val orderDetails: List<DataOrderProduct>,
    val totalPrice: Long,
    val usedPoint: Long,
    val earnedPoint: Long,
)
