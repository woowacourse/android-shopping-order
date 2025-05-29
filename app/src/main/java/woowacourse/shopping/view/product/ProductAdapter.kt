package woowacourse.shopping.view.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productListener: ProductListener,
) : ListAdapter<ProductsItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<ProductsItem>() {
            override fun areItemsTheSame(
                oldItem: ProductsItem,
                newItem: ProductsItem,
            ): Boolean =
                when {
                    oldItem is ProductsItem.ProductItem && newItem is ProductsItem.ProductItem ->
                        oldItem.product.id == newItem.product.id

                    oldItem is ProductsItem.RecentWatchingItem && newItem is ProductsItem.RecentWatchingItem ->
                        oldItem.products.map { it.product.id } == newItem.products.map { it.product.id }

                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: ProductsItem,
                newItem: ProductsItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ProductsItem.ItemType.from(viewType)) {
            ProductsItem.ItemType.PRODUCT -> ProductViewHolder.of(parent, productListener)
            ProductsItem.ItemType.MORE -> ProductMoreViewHolder.of(parent, productListener)
            ProductsItem.ItemType.RECENT_WATCHING ->
                ProductRecentWatchingViewHolder.of(
                    parent,
                    productListener,
                )
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> holder.bind(getItem(position) as ProductsItem.ProductItem)
            is ProductRecentWatchingViewHolder -> holder.bind((getItem(position) as ProductsItem.RecentWatchingItem))
        }
    }

    interface ProductListener :
        ProductViewHolder.ProductClickListener,
        ProductMoreViewHolder.ProductMoreClickListener,
        RecentProductViewHolder.ProductRecentMoreWatchingClickListener
}
