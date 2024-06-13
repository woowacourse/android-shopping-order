package woowacourse.shopping.ui.coupon.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.coupon.CouponListener
import woowacourse.shopping.ui.coupon.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponListener: CouponListener,
) : ViewHolder(binding.root) {
    fun bind(couponUiModel: CouponUiModel) {
        binding.coupon = couponUiModel
        binding.checkBoxCoupon.setOnClickListener {
            couponListener.selectCoupon(couponUiModel.couponId)
        }
    }
}
