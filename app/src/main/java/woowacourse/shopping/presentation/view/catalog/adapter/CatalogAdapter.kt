package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CatalogItem
import woowacourse.shopping.presentation.model.CatalogItem.CatalogType
import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

class CatalogAdapter(
    private val eventListener: CatalogEventListener,
) : ListAdapter<CatalogItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
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

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CatalogItem>() {
                override fun areItemsTheSame(
                    oldItem: CatalogItem,
                    newItem: CatalogItem,
                ): Boolean = oldItem.viewType == newItem.viewType && oldItem.hasSameIdentityAs(newItem)

                override fun areContentsTheSame(
                    oldItem: CatalogItem,
                    newItem: CatalogItem,
                ): Boolean = oldItem == newItem
            }

        private fun CatalogItem.hasSameIdentityAs(other: CatalogItem): Boolean =
            when {
                this is CatalogItem.ProductItem && other is CatalogItem.ProductItem ->
                    this.productId == other.productId

                this is CatalogItem.RecentProducts && other is CatalogItem.RecentProducts ->
                    this.hasSameProductIdsAs(other)

                this is CatalogItem.LoadMoreItem && other is CatalogItem.LoadMoreItem -> true

                else -> false
            }

        private fun CatalogItem.RecentProducts.hasSameProductIdsAs(other: CatalogItem.RecentProducts): Boolean {
            val ids = products.map { it.id }
            val otherIds = other.products.map { it.id }
            return ids == otherIds
        }
    }
}
