package woowacourse.shopping.domain.model

class PercentageDiscountCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        TODO("Not yet implemented")
    }

    override fun getIsAvailable(products: Products): Boolean {
        TODO("Not yet implemented")
    }

    override fun copy(
        detail: CouponDetail,
        isSelected: Boolean,
    ): Coupon = PercentageDiscountCoupon(detail, isSelected)
}
