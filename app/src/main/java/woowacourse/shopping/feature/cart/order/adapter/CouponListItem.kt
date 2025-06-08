package woowacourse.shopping.feature.cart.order.adapter

import woowacourse.shopping.domain.model.coupon.Coupon

sealed class CouponListItem {
    data class CouponData(
        val couponItem: Coupon,
    ) : CouponListItem()

    data object Skeleton : CouponListItem()
}
