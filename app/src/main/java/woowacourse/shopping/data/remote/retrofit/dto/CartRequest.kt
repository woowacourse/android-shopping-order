package woowacourse.shopping.data.remote.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val productId: Long,
    val quantity: Int,
)
