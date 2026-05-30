package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.product.Money
import java.time.LocalTime

@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponResponseDto {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
}

fun CouponResponseDto.toCoupon(): Coupon =
    when (this) {
        is FixedDiscountCouponResponseDto ->
            Coupon.FixedDiscount(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discount = Money(discount),
                minimumAmount = Money(minimumAmount),
            )
        is BuyXGetYCouponResponseDto ->
            Coupon.BuyXGetY(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            )
        is FreeShippingCouponResponseDto ->
            Coupon.FreeShipping(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = Money(minimumAmount),
            )
        is PercentageDiscountCouponResponseDto ->
            Coupon.PercentageDiscount(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discountPercentage = discount,
                availableStartTime = LocalTime.parse(availableTime.start),
                availableEndTime = LocalTime.parse(availableTime.end),
            )
    }
