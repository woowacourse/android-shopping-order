package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("freeShipping")
data class FreeShippingCouponResponseDto(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
) : CouponResponseDto()
