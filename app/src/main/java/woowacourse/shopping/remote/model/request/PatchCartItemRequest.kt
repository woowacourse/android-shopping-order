package woowacourse.shopping.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PatchCartItemRequest(
    val quantity: Int,
)
