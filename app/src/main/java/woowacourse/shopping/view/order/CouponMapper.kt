package woowacourse.shopping.view.order

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
                condition = "최소 주문 금액: $minimumAmount",
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.Bonus ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                condition = "3개씩 담은 제품이 여러개인 경우, 1개당 금액이 가장 비싼 제품에 적용.",
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.FreeShipping ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                condition = "최소 주문 금액: $minimumAmount",
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )

        is Coupon.PercentageDiscount ->
            CouponItem(
                id = id,
                description = description,
                code = code,
                condition = "사용 가능 시간: 오전${availableStartTime}부터 ${availableEndTime}까지",
                expirationDate = formattedDate,
                type = discountType,
                origin = this,
            )
    }
}
