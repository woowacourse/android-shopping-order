package woowacourse.shopping.data.dto.request

import com.google.gson.annotations.SerializedName

data class OrderRequestDto(
    @SerializedName("cartItems")
    val cartItems: List<Long>,

    @SerializedName("paymentAmount")
    val paymentAmount: Int,
)
