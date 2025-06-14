package woowacourse.shopping.view.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.product.Product

class RecentViewedProductsAdapter(
    private val onSelectProduct: (Product) -> Unit,
) : ListAdapter<Product, RecentViewedProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentViewedProductViewHolder =
        RecentViewedProductViewHolder.of(parent, onSelectProduct)

    override fun onBindViewHolder(
        holder: RecentViewedProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
