package woowacourse.shopping.view.order

import androidx.recyclerview.widget.DiffUtil

object CouponDiffUtil : DiffUtil.ItemCallback<CouponViewItem>() {
    override fun areItemsTheSame(oldItem: CouponViewItem, newItem: CouponViewItem): Boolean {
        if (oldItem is CouponViewItem.CouponItem && newItem is CouponViewItem.CouponItem) {
            return oldItem.coupon.id == newItem.coupon.id
        }
        return oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(oldItem: CouponViewItem, newItem: CouponViewItem): Boolean {
        return oldItem == newItem
    }
}
