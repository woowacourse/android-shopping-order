package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CatalogItem
import woowacourse.shopping.presentation.model.CatalogItem.CatalogType
import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

class CatalogAdapter(
    private val eventListener: CatalogEventListener,
) : ListAdapter<CatalogItem, RecyclerView.ViewHolder>(CatalogDiffUtil) {
    private val recentProductsAdapter = RecentProductAdapter(eventListener)

    override fun getItemViewType(position: Int): Int = currentList[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (CatalogType.entries[viewType]) {
            CatalogType.RECENT_PRODUCT ->
                RecentProductContainerViewHolder.from(
                    parent,
                    recentProductsAdapter,
                )

            CatalogType.PRODUCT -> ProductViewHolder.from(parent, eventListener)
            CatalogType.LOAD_MORE -> LoadMoreViewHolder.from(parent, eventListener)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = currentList[position]
        when (holder) {
            is ProductViewHolder -> holder.bind(item as CatalogItem.ProductItem)
            is RecentProductContainerViewHolder -> holder.bind(item as CatalogItem.RecentProducts)
            is LoadMoreViewHolder -> Unit
        }
    }

    interface CatalogEventListener : QuantityChangeListener {
        fun onProductClick(productId: Long)

        fun onLoadMoreClick()
    }
}
