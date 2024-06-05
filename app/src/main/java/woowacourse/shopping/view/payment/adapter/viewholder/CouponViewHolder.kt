package woowacourse.shopping.view.payment.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.view.payment.OnclickPayment

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onClickPayment: OnclickPayment,
):RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon){

    }
}
