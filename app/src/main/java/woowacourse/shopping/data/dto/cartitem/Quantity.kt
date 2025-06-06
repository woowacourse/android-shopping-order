package woowacourse.shopping.data.dto.cartitem

import com.google.gson.annotations.SerializedName

data class Quantity(
    @SerializedName("quantity")
    val value: Int,
)
