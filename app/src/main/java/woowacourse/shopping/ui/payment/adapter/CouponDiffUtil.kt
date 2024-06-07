package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.data.coupon.CouponState

object CouponDiffUtil : DiffUtil.ItemCallback<CouponState>() {
    override fun areItemsTheSame(
        oldItem: CouponState,
        newItem: CouponState,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CouponState,
        newItem: CouponState,
    ): Boolean {
        return oldItem.coupon == newItem.coupon
    }
}
