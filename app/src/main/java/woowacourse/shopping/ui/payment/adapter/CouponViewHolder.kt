package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.model.CouponUiModel
import woowacourse.shopping.ui.payment.OnCouponClickListener

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onCouponClickListener: OnCouponClickListener,
) : ViewHolder(binding.root) {
    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
        binding.onCouponClickListener = onCouponClickListener
    }
}
