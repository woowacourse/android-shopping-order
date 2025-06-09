package woowacourse.shopping.view.order

import woowacourse.shopping.domain.coupon.Coupon
import java.time.LocalDate

data class CouponState(
    val coupon: Coupon,
    val isSelected: Boolean,
    val id: Long = coupon.id,
    val title: String = coupon.description,
    val expirationDate: LocalDate = coupon.explanationDate,
    val minimumOrderPrice: Int? = coupon.minimumAmount,
)
