package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Coupon

object PaymentCouponDiffCallback : DiffUtil.ItemCallback<Coupon>() {
    override fun areItemsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem == newItem
}
