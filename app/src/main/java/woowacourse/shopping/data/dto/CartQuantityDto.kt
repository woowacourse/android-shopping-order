package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class CartQuantityDto(
    @SerializedName("quantity") val quantity: Int,
)
