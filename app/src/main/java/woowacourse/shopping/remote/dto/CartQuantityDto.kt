package woowacourse.shopping.remote.dto

import com.google.gson.annotations.SerializedName

data class CartQuantityDto(
    @SerializedName("quantity") val quantity: Int,
)
