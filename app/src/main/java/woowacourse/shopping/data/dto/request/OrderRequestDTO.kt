package woowacourse.shopping.data.dto.request

import com.google.gson.annotations.SerializedName

class OrderRequestDTO(
    @SerializedName("cartItems")
    val cartItems: List<Long>,

    @SerializedName("paymentAmount")
    val paymentAmount: Int,
)
