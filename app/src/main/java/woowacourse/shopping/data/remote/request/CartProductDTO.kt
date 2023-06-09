package woowacourse.shopping.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductDTO(
    val id: Long,
    val quantity: Int,
    @SerialName("product")
    val productDto: ProductDTO,
)
