package woowacourse.shopping.data.model.dto.request

import com.google.gson.annotations.SerializedName

data class OrderRequestDto(
    @SerializedName("cartItems")
    val cartIds: List<Long>,
    @SerializedName("totalPrice")
    val totalPrice: Int
)
