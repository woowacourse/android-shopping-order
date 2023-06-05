package woowacourse.shopping.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponDTO(
    @SerialName("memberCouponId")
    val id: Long,
    val name: String,
)
