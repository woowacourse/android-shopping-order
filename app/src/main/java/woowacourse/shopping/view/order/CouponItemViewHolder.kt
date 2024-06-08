package woowacourse.shopping.view.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponItemViewHolder(
    private val binding: ItemCouponBinding,
    couponItemClickListener: CouponItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.couponItemClickListener = couponItemClickListener
    }

    fun bind(couponItem: CouponViewItem.CouponItem) {
        binding.couponItem = couponItem
    }
}

