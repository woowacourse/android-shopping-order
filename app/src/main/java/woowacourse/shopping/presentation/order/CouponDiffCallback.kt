package woowacourse.shopping.presentation.order

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponDiffCallback : DiffUtil.ItemCallback<CouponUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean = oldItem.code == newItem.code

    override fun areContentsTheSame(
        oldItem: CouponUiModel,
        newItem: CouponUiModel,
    ): Boolean = oldItem == newItem
}
