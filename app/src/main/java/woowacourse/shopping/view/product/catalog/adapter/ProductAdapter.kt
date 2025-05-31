package woowacourse.shopping.view.product.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.product.catalog.ProductCatalogEventHandler
import woowacourse.shopping.view.product.catalog.adapter.recent.RecentProductListViewHolder
import woowacourse.shopping.view.util.product.ProductViewHolder

class ProductAdapter(
    private val eventHandler: ProductCatalogEventHandler,
) : ListAdapter<ProductCatalogItem, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ProductCatalogItem.ViewType.entries[viewType]) {
            ProductCatalogItem.ViewType.RECENT_PRODUCT ->
                RecentProductListViewHolder.from(parent, eventHandler)

            ProductCatalogItem.ViewType.PRODUCT -> ProductViewHolder.from(parent, eventHandler)
            ProductCatalogItem.ViewType.LOAD_MORE -> LoadMoreViewHolder.from(parent, eventHandler)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is ProductCatalogItem.RecentProductsItem ->
                (holder as RecentProductListViewHolder).bind(item)

            is ProductCatalogItem.ProductItem ->
                (holder as ProductViewHolder).bind(item.product, item.quantity)

            ProductCatalogItem.LoadMoreItem -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<ProductCatalogItem>() {
                override fun areItemsTheSame(
                    oldItem: ProductCatalogItem,
                    newItem: ProductCatalogItem,
                ): Boolean =
                    when {
                        oldItem is ProductCatalogItem.ProductItem &&
                            newItem is ProductCatalogItem.ProductItem ->
                            oldItem.product.id == newItem.product.id

                        oldItem is ProductCatalogItem.RecentProductsItem &&
                            newItem is ProductCatalogItem.RecentProductsItem ->
                            true

                        oldItem is ProductCatalogItem.LoadMoreItem &&
                            newItem is ProductCatalogItem.LoadMoreItem -> true

                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: ProductCatalogItem,
                    newItem: ProductCatalogItem,
                ): Boolean = oldItem == newItem
            }
    }
}
