package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.data.coupon.Coupon

object CouponDiffUtil : DiffUtil.ItemCallback<Coupon>() {
    override fun areItemsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean {
        return oldItem == newItem
    }
}
