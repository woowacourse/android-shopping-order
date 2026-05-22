package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FIXED")
data class FixedDiscountCouponResponseDto(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
    override val discountType: String = "FIXED",
) : CouponResponseDto()
