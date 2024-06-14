package woowacourse.shopping.ui.payment

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCouponBinding
import woowacourse.shopping.ui.model.CouponUi

class CouponViewHolder(
    private val binding: HolderCouponBinding,
    private val couponCheckListener: CouponCheckListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: CouponUi) {
        binding.coupon = coupon
        binding.couponListener = couponCheckListener
    }
}
