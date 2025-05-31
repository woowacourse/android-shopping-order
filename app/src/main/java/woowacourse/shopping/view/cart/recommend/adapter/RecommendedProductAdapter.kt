package woowacourse.shopping.view.cart.recommend.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.util.product.ProductViewHolder

class RecommendedProductAdapter(
    private val eventHandler: ProductViewHolder.EventHandler,
) : ListAdapter<RecommendedProductItem, ProductViewHolder>(diffCallback) {
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

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<RecommendedProductItem>() {
                override fun areItemsTheSame(
                    oldItem: RecommendedProductItem,
                    newItem: RecommendedProductItem,
                ): Boolean = oldItem.product.id == newItem.product.id

                override fun areContentsTheSame(
                    oldItem: RecommendedProductItem,
                    newItem: RecommendedProductItem,
                ): Boolean = oldItem == newItem
            }
    }
}
