package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.Coupon
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResponse(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Long? = null,
    val minimumAmount: Long? = null,
    val availableTime: AvailableTimeResponse? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val discountType: String,
)

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String,
)

fun CouponResponse.toDomain(): Coupon {
    val expiryDate = LocalDate.parse(expirationDate)
    return when (discountType) {
        "fixed" ->
            Coupon.FixedAmount(
                id = id,
                title = description,
                code = code,
                expiryDate = expiryDate,
                amount = discount ?: 0,
                minOrderAmount = minimumAmount ?: 0,
            )

        "percentage" ->
            Coupon.TimeRate(
                id = id,
                title = description,
                code = code,
                expiryDate = expiryDate,
                rate = (discount ?: 0) / 100.0,
                startTime = LocalTime.parse(availableTime?.start ?: "00:00:00"),
                endTime = LocalTime.parse(availableTime?.end ?: "23:59:59"),
            )

        "buyXgetY" ->
            Coupon.BuyOneGetOne(
                id = id,
                title = description,
                code = code,
                expiryDate = expiryDate,
                buyQuantity = buyQuantity ?: 0,
                freeQuantity = getQuantity ?: 0,
            )

        "freeShipping" ->
            Coupon.FreeShipping(
                id = id,
                title = description,
                code = code,
                expiryDate = expiryDate,
                minOrderAmount = minimumAmount ?: 0,
            )

        else -> error("Unsupported coupon type: $discountType")
    }
}
