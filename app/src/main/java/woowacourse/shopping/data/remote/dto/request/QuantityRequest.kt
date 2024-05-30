package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class QuantityRequest(
    val quantity: Int,
)
