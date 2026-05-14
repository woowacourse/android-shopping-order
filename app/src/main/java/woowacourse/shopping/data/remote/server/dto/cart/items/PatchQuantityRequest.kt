package woowacourse.shopping.data.remote.server.dto.cart.items

import kotlinx.serialization.Serializable

@Serializable
data class PatchQuantityRequest(val quantity: Int)