package woowacourse.shopping.feature.cart.order.adapter

import androidx.recyclerview.widget.DiffUtil

class CouponDiffCallback : DiffUtil.ItemCallback<CouponListItem>() {
    override fun areItemsTheSame(
        oldItem: CouponListItem,
        newItem: CouponListItem,
    ): Boolean =
        when {
            oldItem is CouponListItem.Skeleton && newItem is CouponListItem.Skeleton -> true
            oldItem is CouponListItem.CouponData && newItem is CouponListItem.CouponData ->
                oldItem.couponItem.id == newItem.couponItem.id

            else -> false
        }

    override fun areContentsTheSame(
        oldItem: CouponListItem,
        newItem: CouponListItem,
    ): Boolean =
        when {
            oldItem is CouponListItem.CouponData && newItem is CouponListItem.CouponData -> {
                oldItem.couponItem.id == newItem.couponItem.id &&
                    oldItem.couponItem.isSelected == newItem.couponItem.isSelected
            }

            else -> oldItem == newItem
        }
}
