package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class ShoppingProductDto(
    @SerializedName("productId") val productId: Long,
    @SerializedName("quantity") val quantity: Int,
)
