package woowacourse.shopping.data.network.coupon.dto

import java.time.LocalDate
import java.time.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageCoupon

@Serializable
data class CouponDto(
    @SerialName("availableTime")
    val availableTime: AvailableTime,
    @SerialName("buyQuantity")
    val buyQuantity: Int,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discount")
    val discount: Int,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("minimumAmount")
    val minimumAmount: Int,
) {
    fun toDomain(): Coupon? {
        val validUntil = LocalDate.parse(expirationDate)
        return when (discountType) {
            "fixed" -> FixedDiscountCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = minimumAmount,
                discountPrice = discount,
            )

            "buyXgetY" -> BuyXGetYCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            )

            "freeShipping" -> FreeShippingCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = minimumAmount,
            )

            "percentage" -> PercentageCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                discountRate = discount.toDouble(),
                startTime = LocalTime.parse(availableTime.start),
                endTime = LocalTime.parse(availableTime.end),
            )

            else -> null
        }
    }
}
