package woowacourse.shopping.presentation.view.payment.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.common.model.CouponUiModel

object CouponDiffUtil : DiffUtil.ItemCallback<CouponUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean = oldItem == newItem
}
