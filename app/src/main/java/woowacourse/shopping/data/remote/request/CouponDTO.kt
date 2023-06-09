package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class CouponDTO(
    val id: Long,
    val name: String,
)
