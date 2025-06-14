package woowacourse.shopping.view.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductRecentWatchingBinding
import woowacourse.shopping.view.product.RecentProductViewHolder.ProductRecentMoreWatchingClickListener

class ProductRecentWatchingViewHolder(
    binding: ItemProductRecentWatchingBinding,
    productListener: ProductRecentMoreWatchingClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val recentProductAdapter = RecentProductAdapter(productListener)

    init {
        binding.productRecentWatching.apply {
            adapter = recentProductAdapter
        }
    }

    fun bind(recentWatchingItem: ProductsItem.RecentWatchingItem) {
        recentProductAdapter.submitList(recentWatchingItem.products)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            productListener: ProductRecentMoreWatchingClickListener,
        ): ProductRecentWatchingViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemProductRecentWatchingBinding.inflate(layoutInflater, parent, false)
            return ProductRecentWatchingViewHolder(binding, productListener)
        }
    }
}
