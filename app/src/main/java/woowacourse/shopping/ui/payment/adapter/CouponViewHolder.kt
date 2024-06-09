package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.model.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
): ViewHolder(binding.root) {
    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }
}
