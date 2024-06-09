package woowacourse.shopping.view.order.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.view.order.adapter.OnClickCoupon

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onClickCoupon: OnClickCoupon,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
        binding.onClickCoupon = onClickCoupon
    }
}
