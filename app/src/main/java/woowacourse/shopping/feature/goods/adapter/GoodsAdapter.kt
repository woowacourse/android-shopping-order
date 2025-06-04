package woowacourse.shopping.feature.goods.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.feature.goods.adapter.history.HistoryContainerViewHolder
import woowacourse.shopping.feature.model.GoodsItem

class GoodsAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<GoodsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    private var hasNextPage: Boolean = true

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<GoodsItem>() {
                override fun areItemsTheSame(
                    oldItem: GoodsItem,
                    newItem: GoodsItem,
                ): Boolean =
                    when {
                        oldItem is GoodsItem.Recent && newItem is GoodsItem.Recent ->
                            oldItem.histories == newItem.histories
                        oldItem is GoodsItem.Product && newItem is GoodsItem.Product ->
                            oldItem.cart.product.id == newItem.cart.product.id
                        oldItem is GoodsItem.LoadMore && newItem is GoodsItem.LoadMore -> true
                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: GoodsItem,
                    newItem: GoodsItem,
                ): Boolean = oldItem == newItem
            }
    }

    fun setItems(newItems: List<GoodsItem>) {
        val finalItems = if (hasNextPage) newItems + GoodsItem.LoadMore else newItems
        submitList(finalItems)
    }

    fun setHasNextPage(value: Boolean) {
        if (hasNextPage != value) {
            hasNextPage = value
            submitList(
                currentList.filter { it !is GoodsItem.LoadMore }.let {
                    if (hasNextPage) it + GoodsItem.LoadMore else it
                },
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is GoodsItem.Recent -> ItemViewType.HISTORY.type
            is GoodsItem.Product -> ItemViewType.GOODS.type
            is GoodsItem.LoadMore -> ItemViewType.LOAD_MORE.type
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ItemViewType.from(viewType)) {
            ItemViewType.HISTORY -> {
                HistoryContainerViewHolder.create(parent, goodsClickListener)
            }
            ItemViewType.GOODS -> {
                GoodsViewHolder.create(parent, goodsClickListener)
            }
            ItemViewType.LOAD_MORE -> {
                LoadMoreViewHolder.create(parent, goodsClickListener)
            }
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is GoodsItem.Recent -> {
                if (holder is HistoryContainerViewHolder) {
                    holder.bind(item.histories)
                }
            }
            is GoodsItem.Product -> {
                if (holder is GoodsViewHolder) {
                    holder.bind(item.cart)
                }
            }
            is GoodsItem.LoadMore -> {
                if (holder is LoadMoreViewHolder) {
                    holder.bind()
                }
            }
        }
    }
}
