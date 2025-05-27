package woowacourse.shopping.view.product

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.product.RecentProductViewHolder.ProductRecentMoreWatchingClickListener

class RecentProductAdapter(
    private val recentWatchingItems: ProductsItem.RecentWatchingItem,
    private val productListener: ProductRecentMoreWatchingClickListener,
) : RecyclerView.Adapter<RecentProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder = RecentProductViewHolder.of(parent, productListener)

    override fun getItemCount(): Int = recentWatchingItems.products.size

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(recentWatchingItems.products[position])
    }
}
