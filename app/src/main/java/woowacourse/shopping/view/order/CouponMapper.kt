package woowacourse.shopping.view.order

import woowacourse.shopping.R
import woowacourse.shopping.domain.order.Coupon
import java.time.format.DateTimeFormatter

fun Coupon.toUiModel(): CouponItem {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    val formattedDate = expirationDate.format(formatter)

    return when (this) {
        is Coupon.PriceDiscount ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                conditionResId = R.string.coupon_price_discount_condition,
                conditionArgs = listOf(minimumAmount),
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.Bonus,
        ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                conditionResId = R.string.coupon_bonus_discount_condition,
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.FreeShipping ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                conditionResId = R.string.coupon_free_shipping_discount_condition,
                conditionArgs = listOf(minimumAmount),
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.PercentageDiscount ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                conditionResId = R.string.coupon_percentage_discount_condition,
                conditionArgs =
                    listOf(
                        availableStartTime,
                        availableEndTime,
                    ),
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )
    }
}
