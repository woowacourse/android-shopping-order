package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventHandler
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem.CatalogType
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem.LoadMoreItem
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem.ProductItem
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem.RecentProductsItem
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class CatalogAdapter(
    private val catalogEventHandler: CatalogEventHandler,
    private val itemCounterEventHandler: ItemCounterEventHandler,
) : ListAdapter<CatalogItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<CatalogItem>() {
            override fun areItemsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean =
                when {
                    oldItem is RecentProductsItem && newItem is RecentProductsItem -> true
                    oldItem is ProductItem && newItem is ProductItem -> oldItem.product.id == newItem.product.id
                    oldItem is LoadMoreItem && newItem is LoadMoreItem -> true
                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean =
                when {
                    oldItem is RecentProductsItem && newItem is RecentProductsItem -> oldItem.products == newItem.products
                    oldItem is ProductItem && newItem is ProductItem -> oldItem.product == newItem.product
                    oldItem is LoadMoreItem && newItem is LoadMoreItem -> true
                    else -> false
                }
        },
    ) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (CatalogType.entries[viewType]) {
            CatalogType.RECENT_PRODUCTS ->
                RecentProductsViewHolder.from(
                    parent,
                    catalogEventHandler,
                )

            CatalogType.PRODUCT ->
                ProductViewHolder.from(
                    parent,
                    catalogEventHandler,
                    itemCounterEventHandler,
                )

            CatalogType.LOAD_MORE -> LoadMoreViewHolder.from(parent, catalogEventHandler)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is RecentProductsItem -> (holder as RecentProductsViewHolder).bind(item.products)
            is ProductItem -> (holder as ProductViewHolder).bind(item)
            is LoadMoreItem -> Unit
        }
    }

    fun updateItem(productUiModel: ProductUiModel) {
        val updatedList =
            currentList.map { catalogItem ->
                if (catalogItem is ProductItem && catalogItem.product.id == productUiModel.id) {
                    ProductItem(productUiModel)
                } else {
                    catalogItem
                }
            }
        submitList(updatedList)
    }
}
