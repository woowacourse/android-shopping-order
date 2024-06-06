package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class QuantityDto(
    @SerializedName("quantity") val quantity: Int,
)
