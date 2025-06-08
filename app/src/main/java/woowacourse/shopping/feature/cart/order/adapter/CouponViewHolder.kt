package woowacourse.shopping.feature.cart.order.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon

class CouponViewHolder(
    val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
    }
}
