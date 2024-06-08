package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel

class CouponViewHolder(val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        coupon: Coupon,
        viewModel: PaymentViewmodel,
    ) {
        binding.coupon = coupon
        binding.viewModel = viewModel
    }
}
