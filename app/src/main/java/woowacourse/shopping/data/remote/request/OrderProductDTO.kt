package woowacourse.shopping.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderProductDTO(
    @SerialName("productResponse")
    val product: ProductDTO,
    val quantity: Int,
)
