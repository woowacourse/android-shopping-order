package woowacourse.shopping.data.network.coupon.dto

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
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
    fun toDomain(): Coupon {
        val validUntil = parseDate(expirationDate, "expirationDate")
        return when (discountType) {
            "fixed" -> FixedDiscountCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = requireField(minimumAmount, "minimumAmount"),
                discountPrice = requireField(discount, "discount"),
            )

            "buyXgetY" -> BuyXGetYCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                buyQuantity = requireField(buyQuantity, "buyQuantity"),
                getQuantity = requireField(getQuantity, "getQuantity"),
            )

            "freeShipping" -> FreeShippingCoupon(
                id = id.toString(),
                description = description,
                expirationDate = validUntil,
                minimumPrice = requireField(minimumAmount, "minimumAmount"),
            )

            "percentage" -> {
                val time = requireField(availableTime, "availableTime")
                PercentageCoupon(
                    id = id.toString(),
                    description = description,
                    expirationDate = validUntil,
                    discountRate = requireField(discount, "discount").toDouble() * 0.01,
                    startTime = parseTime(time.start, "availableTime.start"),
                    endTime = parseTime(time.end, "availableTime.end"),
                )
            }

            else -> throw IllegalArgumentException("알 수 없는 discountType: $discountType (coupon id=$id)")
        }
    }

    private fun <T : Any> requireField(
        value: T?,
        name: String,
    ): T = value ?: throw IllegalArgumentException("$discountType 쿠폰에 필수 필드 '$name' 누락 (coupon id=$id)")

    private fun parseDate(
        value: String,
        name: String,
    ): LocalDate = try {
        LocalDate.parse(value)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException(
            "$discountType 쿠폰의 '$name' 날짜 포맷 오류: '$value' (coupon id=$id)", e,
        )
    }

    private fun parseTime(
        value: String,
        name: String,
    ): LocalTime = try {
        LocalTime.parse(value)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException(
            "$discountType 쿠폰의 '$name' 시간 포맷 오류: '$value' (coupon id=$id)", e,
        )
    }
}
