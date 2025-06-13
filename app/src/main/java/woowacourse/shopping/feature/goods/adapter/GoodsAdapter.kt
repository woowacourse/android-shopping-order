package woowacourse.shopping.feature.goods.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.feature.goods.GoodsItems
import woowacourse.shopping.feature.goods.adapter.history.HistoryContainerViewHolder

class GoodsAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<GoodsRvItems, RecyclerView.ViewHolder>(GoodsDiffUtil) {
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
        val newItems = mutableListOf<GoodsRvItems>()

        if (items.historyItems.isNotEmpty()) {
            val newHistoryItem = GoodsRvItems.HistoryItem(items.historyItems)
            newItems.add(newHistoryItem)
            newItems.add(GoodsRvItems.DividerItem)
        }

        val newGoodsItems = items.goodsItems.map { GoodsRvItems.GoodsItem(it) }
        newItems.addAll(newGoodsItems)

        if (items.hasNextPage) newItems.add(GoodsRvItems.LoadMoreItem)

        submitList(newItems)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is GoodsRvItems.HistoryItem -> (holder as HistoryContainerViewHolder).bind(item)
            GoodsRvItems.DividerItem -> Unit
            is GoodsRvItems.GoodsItem -> (holder as GoodsViewHolder).bind(item)
            GoodsRvItems.LoadMoreItem -> (holder as LoadMoreViewHolder).bind()
        }
    }
}
