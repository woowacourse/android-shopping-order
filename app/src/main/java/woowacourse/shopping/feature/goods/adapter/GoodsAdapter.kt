package woowacourse.shopping.feature.goods.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.feature.goods.GoodsItems
import woowacourse.shopping.feature.goods.adapter.history.HistoryContainerViewHolder

class GoodsAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<woowacourse.shopping.feature.goods.adapter.ProductFeedItem, RecyclerView.ViewHolder>(GoodsDiffUtil) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.type

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ItemViewType.from(viewType)) {
            ItemViewType.HISTORY -> HistoryContainerViewHolder.of(parent, goodsClickListener)
            ItemViewType.DIVIDER -> DividerViewHolder.of(parent)
            ItemViewType.GOODS -> GoodsViewHolder.of(parent, goodsClickListener)
            ItemViewType.LOAD_MORE -> LoadMoreViewHolder.of(parent, goodsClickListener)
        }

    fun addItems(items: GoodsItems) {
        val newItems = mutableListOf<woowacourse.shopping.feature.goods.adapter.ProductFeedItem>()

        if (items.historyItems.isNotEmpty()) {
            val newHistoryItem = ProductFeedItem.HistoryItem(items.historyItems)
            newItems.add(newHistoryItem)
            newItems.add(ProductFeedItem.DividerItem)
        }

        val newGoodsItems = items.goodsItems.map { ProductFeedItem.GoodsItem(it) }
        newItems.addAll(newGoodsItems)

        if (items.hasNextPage) newItems.add(ProductFeedItem.LoadMoreItem)

        submitList(newItems)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is ProductFeedItem.HistoryItem -> (holder as HistoryContainerViewHolder).bind(item)
            ProductFeedItem.DividerItem -> Unit
            is ProductFeedItem.GoodsItem -> (holder as GoodsViewHolder).bind(item)
            ProductFeedItem.LoadMoreItem -> (holder as LoadMoreViewHolder).bind()
        }
    }
}
