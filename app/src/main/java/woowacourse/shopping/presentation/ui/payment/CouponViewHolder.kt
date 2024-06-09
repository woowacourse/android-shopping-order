package woowacourse.shopping.presentation.ui.payment

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val clickListener: CouponEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
        binding.clickListener = clickListener
    }

    fun onIsCheckedChanged(coupon: CouponUiModel) {
        binding.coupon = coupon
    }
}
