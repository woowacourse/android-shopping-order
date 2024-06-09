package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel

class CouponViewHolder(val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        couponState: CouponState,
        viewModel: PaymentViewmodel,
    ) {
        binding.couponState = couponState
        binding.viewModel = viewModel
    }
}
