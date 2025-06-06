package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeLimitedCouponResponse(
    @SerialName("availableTime")
    val availableTime: AvailableTimeResponse,
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("discount")
    val discount: Int,
    @SerialName("discountType")
    override val discountType: String,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("id")
    override val id: Long,
) : CouponResponse()
