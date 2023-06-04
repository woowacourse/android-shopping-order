package woowacourse.shopping.data.remote.response

import com.google.gson.annotations.SerializedName

data class CartProductDto(
    val id: Long,
    val quantity: Int,
    @SerializedName("product")
    val productResponseDto: ProductResponseDto,
)
