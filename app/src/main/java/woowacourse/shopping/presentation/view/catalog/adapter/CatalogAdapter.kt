package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class CatalogAdapter(
    private val eventListener: CatalogEventListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<CatalogItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (CatalogItem.CatalogType.entries[viewType]) {
            CatalogItem.CatalogType.RECENT_PRODUCTS ->
                RecentProductViewHolder.from(
                    parent,
                    eventListener,
                )

            CatalogItem.CatalogType.PRODUCT ->
                ProductViewHolder.from(
                    parent,
                    eventListener,
                )

            CatalogItem.CatalogType.LOAD_MORE -> LoadMoreViewHolder.from(parent, eventListener)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is RecentProductViewHolder -> {
                val item = items[position] as CatalogItem.RecentProductsItem
                holder.bind(item.products)
            }
            is ProductViewHolder -> holder.bind(items[position] as CatalogItem.ProductItem)
            is LoadMoreViewHolder -> Unit
        }
    }

    fun updateProducts(items: List<CatalogItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(productUiModel: ProductUiModel) {
        val index =
            items.indexOfFirst { item ->
                item is CatalogItem.ProductItem && item.product.id == productUiModel.id
            }

        if (index != -1) {
            items[index] = CatalogItem.ProductItem(productUiModel)
            notifyItemChanged(index)
        }
    }
}
