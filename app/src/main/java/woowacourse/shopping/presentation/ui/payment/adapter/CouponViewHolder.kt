package woowacourse.shopping.presentation.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.presentation.ui.payment.PaymentHandler

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val paymentHandler: PaymentHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Coupon) {
        binding.coupon = item
        binding.handler = paymentHandler
    }
}
