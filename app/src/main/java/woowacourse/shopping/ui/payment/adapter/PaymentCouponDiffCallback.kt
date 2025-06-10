package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Coupon

object PaymentCouponDiffCallback : DiffUtil.ItemCallback<Coupon>() {
    override fun areItemsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem.detail.id == newItem.detail.id

    override fun areContentsTheSame(
        oldItem: Coupon,
        newItem: Coupon,
    ): Boolean = oldItem.detail == newItem.detail && oldItem.isSelected == newItem.isSelected
}
