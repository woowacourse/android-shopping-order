package woowacourse.shopping.feature.goods.adapter

import androidx.recyclerview.widget.DiffUtil

val GoodsDiffUtil =
    object : DiffUtil.ItemCallback<GoodsRvItems>() {
        override fun areItemsTheSame(
            oldItem: GoodsRvItems,
            newItem: GoodsRvItems,
        ): Boolean {
            if (oldItem::class != newItem::class) return false
            when (oldItem) {
                is GoodsRvItems.GoodsItem -> {
                    if (newItem !is GoodsRvItems.GoodsItem) return false
                    return oldItem.item.product.id == newItem.item.product.id
                }
                is GoodsRvItems.HistoryItem -> {
                    if (newItem !is GoodsRvItems.HistoryItem) return false
                    return oldItem.items.map { it.id } == newItem.items.map { it.id }
                }
                else -> return true
            }
        }

        override fun areContentsTheSame(
            oldItem: GoodsRvItems,
            newItem: GoodsRvItems,
        ): Boolean = oldItem == newItem
    }
