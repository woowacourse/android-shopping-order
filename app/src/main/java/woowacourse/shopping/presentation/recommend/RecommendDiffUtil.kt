package woowacourse.shopping.presentation.recommend

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.CartItemUiModel

object RecommendDiffUtil : DiffUtil.ItemCallback<CartItemUiModel>() {
    override fun areItemsTheSame(
        oldItem: CartItemUiModel,
        newItem: CartItemUiModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: CartItemUiModel,
        newItem: CartItemUiModel,
    ): Boolean = oldItem == newItem
}
