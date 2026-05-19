package woowacourse.shopping.data.source.remote.dto.cart.request

import kotlinx.serialization.Serializable

@Serializable
data class QuantityRequest(
    val quantity: Int,
)
