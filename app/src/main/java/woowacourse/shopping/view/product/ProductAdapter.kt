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
    private var items: MutableMap<ProductsItem.ItemType, List<ProductsItem>> =
        mutableMapOf(
            ProductsItem.ItemType.PRODUCT to emptyList(),
            ProductsItem.ItemType.RECENT_WATCHING to emptyList(),
        )

    private val existsAllViewType: Boolean
        get() =
            items.values.all { it.isNotEmpty() }

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

    override fun submitList(item: List<ProductsItem>?) {
        if (item.isNullOrEmpty()) return
        val viewType = item.first().viewType
        items[viewType] = item

        if (existsAllViewType) {
            super.submitList(items.flatMap { entry -> entry.value })
        }
    }

    interface ProductListener :
        ProductViewHolder.ProductClickListener,
        ProductMoreViewHolder.ProductMoreClickListener,
        RecentProductViewHolder.ProductRecentMoreWatchingClickListener
}
