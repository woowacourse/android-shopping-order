package woowacourse.shopping.view.payment

import woowacourse.shopping.domain.payment.Coupon

sealed interface CouponsItem {
    val viewType: CouponsItemViewType

    data object Header : CouponsItem {
        override val viewType: CouponsItemViewType = CouponsItemViewType.HEADER
    }

    data class CouponItem(
        val value: Coupon,
        val selected: Boolean,
    ) : CouponsItem {
        override val viewType: CouponsItemViewType = CouponsItemViewType.COUPON

        val minimumAmount: Int? =
            when (value) {
                is Coupon.BuyNGetNCoupon -> null
                is Coupon.FixedDiscountCoupon -> value.minimumAmount
                is Coupon.FreeShippingCoupon -> value.minimumAmount
                is Coupon.PercentageCoupon -> null
            }
    }
}
