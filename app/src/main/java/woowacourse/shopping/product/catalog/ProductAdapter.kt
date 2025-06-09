package woowacourse.shopping.product.catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productActionListener: ProductActionListener,
    private val quantityControlListener: QuantityControlListener,
) : ListAdapter<CatalogItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<CatalogItem>() {
            override fun areItemsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean =
                when {
                    oldItem is CatalogItem.ProductItem && newItem is CatalogItem.ProductItem ->
                        oldItem.productItem.id == newItem.productItem.id

                    oldItem is CatalogItem.LoadMoreButtonItem && newItem is CatalogItem.LoadMoreButtonItem -> true
                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_PRODUCT ->
                ProductViewHolder.from(parent, productActionListener, quantityControlListener)

            else -> LoadButtonViewHolder.from(parent, productActionListener)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind((currentList[position] as CatalogItem.ProductItem).productItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (currentList[position]) {
            CatalogItem.LoadMoreButtonItem -> VIEW_TYPE_LOAD_MORE
            is CatalogItem.ProductItem -> VIEW_TYPE_PRODUCT
        }

    fun updateItem(product: ProductUiModel) {
        val currentList = currentList.toMutableList()
        val index =
            currentList.indexOfFirst {
                it is CatalogItem.ProductItem && it.productItem.id == product.id
            }
        if (index != -1) {
            currentList[index] = CatalogItem.ProductItem(product)
            submitList(currentList)
        }
    }

    companion object {
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }
}
