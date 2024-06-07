package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.coupon.CouponState
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.payment.CouponClickListener

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponClickListener: CouponClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(couponState: CouponState) {
        binding.couponState = couponState
        binding.couponClickListener = couponClickListener
    }
}
