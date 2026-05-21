package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class AddCartRequestBody(
    val productId: Long,
    val quantity: Int,
)
