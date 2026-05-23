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
    val availableTime: AvailableTime? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
) {
    fun toDomain(): Coupon? {
        val validUntil = LocalDate.parse(expirationDate)
        return when (discountType) {
            "fixed" -> FixedDiscountCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = minimumAmount ?: 0,
                discountPrice = discount ?: 0,
            )

            "buyXgetY" -> BuyXGetYCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                buyQuantity = buyQuantity ?: 0,
                getQuantity = getQuantity ?: 0,
            )

            "freeShipping" -> FreeShippingCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = minimumAmount ?: 0,
            )

            "percentage" -> {
                val time = availableTime ?: return null
                PercentageCoupon(
                    id = id.toString(),
                    description = description,
                    expirationDate = validUntil,
                    discountRate = (discount ?: 0).toDouble() * 0.01,
                    startTime = LocalTime.parse(time.start),
                    endTime = LocalTime.parse(time.end),
                )
            }

            else -> null
        }
    }
}
