package woowacourse.shopping.view.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentWatchingBinding

class RecentProductViewHolder(
    private val binding: ItemRecentWatchingBinding,
    productListener: ProductRecentMoreWatchingClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.recentMoreWatchingClickListener = productListener
    }

    fun bind(productItem: ProductsItem.ProductItem) {
        binding.productItem = productItem
    }

    companion object {
        fun of(
            parent: ViewGroup,
            productListener: ProductRecentMoreWatchingClickListener,
        ): RecentProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecentWatchingBinding.inflate(layoutInflater, parent, false)
            return RecentProductViewHolder(binding, productListener)
        }
    }

    interface ProductRecentMoreWatchingClickListener {
        fun onRecentProductClick(productItem: ProductsItem.ProductItem)
    }
}
