package woowacourse.shopping.data.source.remote.dto.coupon.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponResponse {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
}

@Serializable
@SerialName("fixed")
data class FixedCouponResponse(
    val discount: Long,
    val minimumAmount: Long,
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
) : CouponResponse()

@Serializable
@SerialName("percentage")
data class PercentageCouponResponse(
    val discount: Int,
    val availableTime: AvailableTimeResponse,
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
) : CouponResponse()

@Serializable
@SerialName("buyXgetY")
data class BuyXgetYCouponResponse(
    val buyQuantity: Long,
    val getQuantity: Long,
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
) : CouponResponse()

@Serializable
@SerialName("freeShipping")
data class FreeShippingCouponResponse(
    val minimumAmount: Long,
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
) : CouponResponse()

@Serializable
data class AvailableTimeResponse(
    val end: String,
    val start: String,
)
