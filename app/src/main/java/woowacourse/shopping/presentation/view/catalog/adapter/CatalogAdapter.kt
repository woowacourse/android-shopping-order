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
) : ListAdapter<CatalogItem, RecyclerView.ViewHolder>(DiffCallBack) {
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

        fun onQuantitySelectorOpenButtonClick(productId: Long)
    }

    object DiffCallBack : DiffUtil.ItemCallback<CatalogItem>() {
        override fun areItemsTheSame(
            oldItem: CatalogItem,
            newItem: CatalogItem,
        ): Boolean {
            if (oldItem.viewType != newItem.viewType) return false
            return when (oldItem) {
                is CatalogItem.ProductItem -> oldItem.productId == (newItem as CatalogItem.ProductItem).productId
                CatalogItem.LoadMoreItem -> return false
                is CatalogItem.RecentProducts ->
                    oldItem.products.map { it.id } ==
                        (newItem as CatalogItem.RecentProducts).products.map { it.id }
            }
        }

        override fun areContentsTheSame(
            oldItem: CatalogItem,
            newItem: CatalogItem,
        ): Boolean {
            if (oldItem.viewType != newItem.viewType) return false
            return when (oldItem) {
                is CatalogItem.ProductItem -> oldItem == (newItem as CatalogItem.ProductItem)
                CatalogItem.LoadMoreItem -> return false
                is CatalogItem.RecentProducts -> oldItem == (newItem as CatalogItem.RecentProducts)
            }
        }
    }
}
