package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PERCENTAGE")
data class PercentageDiscountCouponResponseDto(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTimeDto,
    override val discountType: String = "PERCENTAGE",
) : CouponResponseDto()
