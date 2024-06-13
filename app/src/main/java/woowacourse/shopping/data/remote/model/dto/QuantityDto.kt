package woowacourse.shopping.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class QuantityDto(
    @SerializedName("quantity") val quantity: Int,
)
