package woowacourse.shopping.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCartCountRequest(
    val quantity: Int,
)
