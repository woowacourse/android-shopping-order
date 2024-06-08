package woowacourse.shopping.view.order

interface CouponItemClickListener {
    fun changeCouponSelection(
        isSelected: Boolean,
        couponId: Int,
    )
}
