package woowacourse.shopping.presentation.order.payment

import woowacourse.shopping.domain.entity.coupon.Coupon

data class CouponUiModel(
    val coupon: Coupon,
    val expirationDate: String,
    val minimumPrice: Long,
    val isSelected: Boolean = false,
) {
    val id: Long get() = coupon.id
    val name: String get() = coupon.description
}
