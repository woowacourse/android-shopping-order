package woowacourse.shopping.dto

import com.google.gson.annotations.SerializedName

data class QuantityDto(
    @SerializedName("quantity") val quantity: Int = 0
)
