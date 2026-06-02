package woowacourse.shopping.data.remote.server.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedAmountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.NplusMFreeCoupon
import woowacourse.shopping.domain.model.coupon.TimeBasedPercentCoupon
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
    @SerialName("availableTime")
    val availableTime: AvailableTimeResponse? = null,
)

@Serializable
data class AvailableTimeResponse(
    @SerialName("start")
    val start: String,
    @SerialName("end")
    val end: String,
)

fun CouponResponse.toDomainCoupon(): Coupon {
    val parsedExpirationDate = LocalDate.parse(expirationDate)
    return when {
        isFixedAmountCoupon() ->
            FixedAmountCoupon(
                code = code,
                name = description,
                expirationDate = parsedExpirationDate,
                discountAmount = requireNotNull(discount),
                minimumOrderAmount = requireNotNull(minimumAmount),
            )

        isNplusMFreeCoupon() ->
            NplusMFreeCoupon(
                code = code,
                name = description,
                expirationDate = parsedExpirationDate,
                purchaseQuantity = requireNotNull(buyQuantity),
                freeQuantity = requireNotNull(getQuantity),
            )

        isFreeShippingCoupon() ->
            FreeShippingCoupon(
                code = code,
                name = description,
                expirationDate = parsedExpirationDate,
                minimumOrderAmount = requireNotNull(minimumAmount),
            )

        isTimeBasedPercentCoupon() -> {
            val time = requireNotNull(availableTime)
            TimeBasedPercentCoupon(
                code = code,
                name = description,
                expirationDate = parsedExpirationDate,
                discountRate = requireNotNull(discount).toDiscountRate(),
                startTime = LocalTime.parse(time.start),
                endTime = LocalTime.parse(time.end),
            )
        }

        else -> error("지원하지 않는 쿠폰 유형입니다: $discountType")
    }
}

private fun CouponResponse.isFixedAmountCoupon(): Boolean = discountType == "fixed"

private fun CouponResponse.isNplusMFreeCoupon(): Boolean = discountType == "buyXgetY"

private fun CouponResponse.isFreeShippingCoupon(): Boolean = discountType == "freeShipping"

private fun CouponResponse.isTimeBasedPercentCoupon(): Boolean = discountType == "percentage"

private fun Int.toDiscountRate(): Double = coerceAtLeast(0) / 100.0
