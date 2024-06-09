package woowacourse.shopping.view.order

sealed class CouponViewItem(
    open val viewType: Int,
) {
    data class Loading(override val viewType: Int = VIEW_TYPE_LOADING) : CouponViewItem(viewType)

    data class CouponItem(
        val coupon: woowacourse.shopping.domain.model.Coupon,
        val isSelected: Boolean = false,
        override val viewType: Int = VIEW_TYPE_COUPON,
    ) : CouponViewItem(viewType) {
        fun select(): CouponItem {
            return this.copy(isSelected = !isSelected)
        }
    }

    companion object {
        const val VIEW_TYPE_LOADING = 1000
        const val VIEW_TYPE_COUPON = 1001
    }
}
