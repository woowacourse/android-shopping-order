package woowacourse.shopping.feature.goods.adapter.history

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Cart

class HistoryDiffUtil : DiffUtil.ItemCallback<Cart>() {
    override fun areItemsTheSame(
        oldItem: Cart,
        newItem: Cart,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Cart,
        newItem: Cart,
    ): Boolean = oldItem == newItem
}
