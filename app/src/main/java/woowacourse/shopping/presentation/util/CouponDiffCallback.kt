package woowacourse.shopping.presentation.util

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.data.model.Coupon

class CouponDiffCallback : DiffUtil.ItemCallback<Coupon>() {
    override fun areItemsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem == newItem
}