package woowacourse.shopping.data.network.response.carts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Int,
    val product: Product,
    val quantity: Int
)
