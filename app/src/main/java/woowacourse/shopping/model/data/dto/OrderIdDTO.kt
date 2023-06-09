package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class OrderIdDTO(
    @SerializedName("orderId")
    val orderId: Long
)
