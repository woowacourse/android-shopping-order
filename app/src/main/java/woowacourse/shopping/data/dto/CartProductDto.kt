package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class CartProductDto(
    @SerializedName("id")
    val cartId: Long,

    @SerializedName("product")
    val product: ProductDto,

    @SerializedName("quantity")
    var count: Int,
)
