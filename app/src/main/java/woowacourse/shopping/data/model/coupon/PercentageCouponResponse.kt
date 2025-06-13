package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("percentage")
data class PercentageCouponResponse(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTime,
    override val discountType: String = "percentage",
) : CouponResponse
