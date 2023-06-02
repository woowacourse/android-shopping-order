package woowacourse.shopping.data.remote.request

import com.google.gson.annotations.SerializedName

data class CartProductDTO(
    val id: Long,
    val quantity: Int,
    @SerializedName("product")
    val productDto: ProductDTO,
)
