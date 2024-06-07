package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.payment.CouponClickListener
import woowacourse.shopping.ui.payment.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponClickListener: CouponClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(couponUiModel: CouponUiModel) {
        binding.couponUiModel = couponUiModel
        binding.couponClickListener = couponClickListener
    }
}
