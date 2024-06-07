package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.coupon.CouponState
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(private val binding: ItemCouponBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(couponState: CouponState) {
        binding.couponState = couponState
    }
}
