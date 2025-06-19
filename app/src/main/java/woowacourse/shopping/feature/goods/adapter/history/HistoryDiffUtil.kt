package woowacourse.shopping.feature.goods.adapter.history

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Product

class HistoryDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem == newItem
}
