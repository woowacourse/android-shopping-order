package woowacourse.shopping.data.remote.request

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.remote.response.ProductResponseDto

data class CartProductDto(
    val id: Long,
    val quantity: Int,
    @SerializedName("product")
    val productResponseDto: ProductResponseDto,
)
