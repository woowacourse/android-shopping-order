package woowacourse.shopping.data.coupon.remote.dto

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
sealed class CouponResponseDto {
    abstract val id: Int
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
}

@Serializable
@SerialName("fixed")
data class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
) : CouponResponseDto()

@Serializable
@SerialName("buyXgetY")
data class BuyXGetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : CouponResponseDto()

@Serializable
@SerialName("freeShipping")
data class FreeShippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
) : CouponResponseDto()

@Serializable
@SerialName("percentage")
data class PercentageCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTimeResponseDto,
) : CouponResponseDto()
