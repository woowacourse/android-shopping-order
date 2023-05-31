package woowacourse.shopping.data.model.dto.response

import com.google.gson.annotations.SerializedName

data class OrderDetailDto(
    @SerializedName("priceBeforeDiscount")
    val priceBeforeDiscount: Int,
    @SerializedName("priceAfterDiscount")
    val priceAfterDiscount: Int,
    @SerializedName("products")
    val products: List<OrderProductDto>
)
