package woowacourse.shopping.feature.goods.adapter

import androidx.recyclerview.widget.DiffUtil

val GoodsDiffUtil =
    object : DiffUtil.ItemCallback<ProductFeedItem>() {
        override fun areItemsTheSame(
            oldItem: ProductFeedItem,
            newItem: ProductFeedItem,
        ): Boolean {
            if (oldItem::class != newItem::class) return false
            when (oldItem) {
                is ProductFeedItem.GoodsItem -> {
                    if (newItem !is ProductFeedItem.GoodsItem) return false
                    return oldItem.item.product.id == newItem.item.product.id
                }
                is ProductFeedItem.HistoryItem -> {
                    if (newItem !is ProductFeedItem.HistoryItem) return false
                    return oldItem.items.map { it.id } == newItem.items.map { it.id }
                }
                else -> return true
            }
        }

        override fun areContentsTheSame(
            oldItem: ProductFeedItem,
            newItem: ProductFeedItem,
        ): Boolean = oldItem == newItem
    }
