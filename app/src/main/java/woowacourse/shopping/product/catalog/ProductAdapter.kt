package woowacourse.shopping.product.catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    products: List<CatalogItem>,
    private val productActionListener: ProductActionListener,
    private val quantityControlListener: QuantityControlListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val products: MutableList<CatalogItem> = products.toMutableList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_PRODUCT ->
                ProductViewHolder.from(parent, productActionListener, quantityControlListener)

            VIEW_TYPE_LOAD_MORE -> LoadButtonViewHolder.from(parent, productActionListener)

            else -> LoadingStateProductViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind((products[position] as CatalogItem.ProductItem).productItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (products[position]) {
            CatalogItem.LoadMoreButtonItem -> VIEW_TYPE_LOAD_MORE
            is CatalogItem.ProductItem -> VIEW_TYPE_PRODUCT
            is CatalogItem.LoadingStateProductItem -> VIEW_TYPE_LOADING_PRODUCT
        }

    fun clearItems() {
        products.clear()
    }

    fun addLoadedItems(items: List<CatalogItem>) {
        if (products.lastOrNull() is CatalogItem.LoadMoreButtonItem) {
            products.removeAt(products.lastIndex)
            notifyItemRemoved(products.lastIndex + 1)
        }
        products.addAll(items)
        notifyItemRangeInserted(products.lastIndex, items.size)
    }

    fun updateItem(product: ProductUiModel) {
        val index: Int =
            products.indexOfFirst { (it as CatalogItem.ProductItem).productItem.id == product.id }
        products[index] = CatalogItem.ProductItem(product)
        notifyItemChanged(index)
    }

    override fun getItemCount(): Int = products.size

    companion object {
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
        private const val VIEW_TYPE_LOADING_PRODUCT = 3
    }
}
