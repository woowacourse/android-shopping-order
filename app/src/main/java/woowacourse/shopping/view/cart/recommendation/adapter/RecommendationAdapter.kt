package woowacourse.shopping.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.util.product.ProductViewHolder

class RecommendationAdapter(
    items: List<ProductItem> = emptyList(),
    private val eventHandler: ProductViewHolder.EventHandler,
) : RecyclerView.Adapter<ProductViewHolder>() {
    private val items = items.toMutableList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder = ProductViewHolder.from(parent, eventHandler)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        val item = items[position]
        holder.bind(item.product, item.quantity)
    }

    fun updateItems(newItems: List<ProductItem>) {
        if (newItems.size > items.size) {
            items.addAll(newItems.subList(items.size, newItems.size))
            notifyItemRangeInserted(0, newItems.size)
            return
        }
        val oldItems = items.toList()
        items.clear()
        items.addAll(newItems)

        for (i in items.indices) {
            val oldItem = oldItems[i]
            val newItem = newItems[i]

            if (oldItem != newItem) {
                notifyItemChanged(i)
            }
        }
    }
}
