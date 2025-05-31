package woowacourse.shopping.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.util.product.ProductViewHolder

class RecommendationAdapter(
    private val eventHandler: ProductViewHolder.EventHandler,
) : ListAdapter<ProductItem, ProductViewHolder>(
        object : DiffUtil.ItemCallback<ProductItem>() {
            override fun areItemsTheSame(
                oldItem: ProductItem,
                newItem: ProductItem,
            ): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(
                oldItem: ProductItem,
                newItem: ProductItem,
            ): Boolean {
                return oldItem == newItem
            }
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder = ProductViewHolder.from(parent, eventHandler)

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item.product, item.quantity)
    }

    fun updateItems(newItems: List<ProductItem>) {
        submitList(newItems)
    }
}
