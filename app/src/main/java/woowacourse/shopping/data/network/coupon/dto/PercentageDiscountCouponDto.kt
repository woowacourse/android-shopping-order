package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("percentage")
data class PercentageDiscountCouponDto(
    override val id: Long? = null,
    override val code: String = "",
    override val description: String,
    override val expirationDate: String,
    val discount: Int? = null,
    val availableTime: AvailableTime? = null,
    override val discountType: String? = "percentage",
) : CouponDto
