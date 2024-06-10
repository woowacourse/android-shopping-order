package woowacourse.shopping.view.coupon.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.view.coupon.OnClickCoupon

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onClickCoupon: OnClickCoupon,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
        binding.onClickPayment = onClickCoupon
        if (coupon.minimumAmount != 0) {
            binding.minimumAmount.text =
                binding.root.context.getString(
                    R.string.coupon_min_price,
                    coupon.minimumAmount,
                )
        }
    }
}
