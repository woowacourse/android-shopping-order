package woowacourse.shopping.view.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.product.RecentProductViewHolder.ProductRecentMoreWatchingClickListener

class RecentProductAdapter(
    private val productListener: ProductRecentMoreWatchingClickListener,
) : ListAdapter<ProductsItem.ProductItem, RecentProductViewHolder>(
        object : DiffUtil.ItemCallback<ProductsItem.ProductItem>() {
            override fun areItemsTheSame(
                oldItem: ProductsItem.ProductItem,
                newItem: ProductsItem.ProductItem,
            ): Boolean = oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(
                oldItem: ProductsItem.ProductItem,
                newItem: ProductsItem.ProductItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder = RecentProductViewHolder.of(parent, productListener)

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
