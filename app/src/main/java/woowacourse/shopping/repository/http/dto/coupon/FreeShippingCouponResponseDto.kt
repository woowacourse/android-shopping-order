package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FREE_SHIPPING")
data class FreeShippingCouponResponseDto(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
    override val discountType: String = "FREE_SHIPPING",
) : CouponResponseDto()
