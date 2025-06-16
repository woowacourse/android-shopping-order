package woowacourse.shopping.feature.order

import androidx.recyclerview.widget.DiffUtil

class CouponDiffUtil : DiffUtil.ItemCallback<CouponState>() {
    override fun areItemsTheSame(
        oldItem: CouponState,
        newItem: CouponState,
    ): Boolean = oldItem.item.id == newItem.item.id

    override fun areContentsTheSame(
        oldItem: CouponState,
        newItem: CouponState,
    ): Boolean = oldItem == newItem
}
