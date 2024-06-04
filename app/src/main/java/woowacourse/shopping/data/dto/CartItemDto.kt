package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val productDto: ProductDto,
)
