package woowacourse.shopping.data.remote.server.dto.coupon.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentCoupon
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResultItem(
    @SerialName("availableTime")
    val availableTime: AvailableTime? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discount")
    val discount: Int = 0,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int = 0,
    @SerialName("id")
    val id: Long,
    @SerialName("minimumAmount")
    val minimumAmount: Int = 0,
)

fun CouponResultItem.toDomain(): Coupon {
    val expiryDate = LocalDate.parse(expirationDate)
    return when (discountType) {
        "fixed" ->
            FixedCoupon(
                id = id.toInt(),
                code = code,
                expirationDate = expiryDate,
                discountAmount = discount,
                minimumAmount = minimumAmount,
                description = description,
            )
        "buyXgetY" ->
            BuyXGetYCoupon(
                id = id.toInt(),
                code = code,
                description = description,
                expirationDate = expiryDate,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            )
        "freeShipping" ->
            FreeShippingCoupon(
                id = id.toInt(),
                code = code,
                description = description,
                expirationDate = expiryDate,
                minimumAmount = minimumAmount,
            )
        "percentage" ->
            PercentCoupon(
                id = id.toInt(),
                code = code,
                description = description,
                expirationDate = expiryDate,
                discountPercent = discount / 100.0,
                startTime = LocalTime.parse(availableTime?.start),
                endTime = LocalTime.parse(availableTime?.end),
            )
        else -> throw IllegalArgumentException("Unknown discount type: $discountType")
    }
}
