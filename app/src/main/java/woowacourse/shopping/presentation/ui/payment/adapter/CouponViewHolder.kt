package woowacourse.shopping.presentation.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.ui.CouponModel
import woowacourse.shopping.presentation.ui.payment.PaymentHandler

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val paymentHandler: PaymentHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponModel) {
        binding.couponModel = item
        binding.handler = paymentHandler
    }
}
