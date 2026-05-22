package woowacourse.shopping.repository.http.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Coupon
import java.time.LocalDate

@Serializable
data class CouponListResponseDto(
    val coupons: List<CouponResponseDto> = emptyList(),
)

@Serializable
data class CouponResponseDto(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val expirationDate: String,
    val minimumOrderAmount: Int? = null,
    val fixedDiscountAmount: Int? = null,
    val percentageDiscountRate: Int? = null,
    val requiredSameProductQuantity: Int? = null,
    val freeShipping: Boolean = false,
    val bogoEligible: Boolean = false,
    @SerialName("availableFromHour")
    val availableFromHour: Int? = null,
    @SerialName("availableToHourExclusive")
    val availableToHourExclusive: Int? = null,
) {
    fun toDomain(): Coupon =
        Coupon(
            id = id,
            code = code,
            title = title,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumOrderAmount = minimumOrderAmount,
            fixedDiscountAmount = fixedDiscountAmount,
            percentageDiscountRate = percentageDiscountRate,
            requiredSameProductQuantity = requiredSameProductQuantity,
            freeShipping = freeShipping,
            bogoEligible = bogoEligible,
            availableFromHour = availableFromHour,
            availableToHourExclusive = availableToHourExclusive,
        )
}
