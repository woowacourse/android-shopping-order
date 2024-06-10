package woowacourse.shopping.view.order.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.view.order.adapter.OnClickCoupon
import woowacourse.shopping.view.order.model.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onClickCoupon: OnClickCoupon,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
        binding.onClickCoupon = onClickCoupon
    }
}
