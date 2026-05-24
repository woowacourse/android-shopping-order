package woowacourse.shopping.ui.pay

import woowacourse.shopping.model.AvailableTime
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.calculate
import woowacourse.shopping.ui.util.formattedPrice
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val id: String,
    val code: String,
    val description: String,
    val detail: String,
    val expirationDate: String,
    val discountAmount: String,
    val disabledReason: String? = null,
    val isApplicable: Boolean = true,
    val isSelected: Boolean = false,
)

fun Coupon.toUiModel(
    selectedCouponId: String?,
    cartItems: List<CartItem>,
    now: LocalTime = LocalTime.now(),
): CouponUiModel {
    val calculationResult = calculate(cartItems = cartItems, now = now)

    return CouponUiModel(
        id = id,
        code = code,
        description = description,
        detail = detailText(),
        expirationDate = "만료일 ${expirationDate.format(expirationDateFormatter)}까지",
        discountAmount = discountText(calculationResult.discountAmount.amount),
        disabledReason = disabledReason(calculationResult.isApplicable),
        isApplicable = calculationResult.isApplicable,
        isSelected = calculationResult.isApplicable && id == selectedCouponId,
    )
}

private fun Coupon.detailText(): String =
    when (this) {
        is Coupon.Fixed -> "${formattedPrice(minimumAmount.amount)} 이상 구매 시 사용 가능"
        is Coupon.Percentage -> "${availableTime.toUiText()} 사용 가능"
        is Coupon.BuyXGetY -> "${buyQuantity}개 구매 시 ${getQuantity}개 증정"
        is Coupon.FreeShipping -> "${formattedPrice(minimumAmount.amount)} 이상 구매 시 배송비 무료"
    }

private fun Coupon.discountText(discountAmount: Long): String =
    when (this) {
        is Coupon.FreeShipping -> "배송비 무료"
        else -> "${formattedPrice(discountAmount)} 할인"
    }

private fun Coupon.disabledReason(isApplicable: Boolean): String? {
    if (isApplicable) return null

    return when (this) {
        is Coupon.Fixed -> "최소 주문 금액을 충족하지 않습니다."
        is Coupon.Percentage -> "현재 사용할 수 없는 시간대입니다."
        is Coupon.BuyXGetY -> "적용 가능한 수량의 상품이 없습니다."
        is Coupon.FreeShipping -> "무료 배송 최소 금액을 충족하지 않습니다."
    }
}

private fun AvailableTime.toUiText(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return "${start.format(formatter)}-${end.format(formatter)}"
}

private val expirationDateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
