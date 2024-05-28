package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val id: Int,
    val quantity: Int,
    val product: Product
)
