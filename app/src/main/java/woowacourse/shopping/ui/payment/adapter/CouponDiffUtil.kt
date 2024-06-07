package woowacourse.shopping.ui.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.payment.CouponUiModel

object CouponDiffUtil : DiffUtil.ItemCallback<CouponUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
