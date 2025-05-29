package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener

class RecommendAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val quantityChangeListener: QuantityChangeListener,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    private val items = mutableListOf<CartItem>()

    fun setItems(newItems: List<CartItem>) {
        val old = items.toList()
        items.clear()
        items.addAll(newItems)
        if (old.isEmpty()) {
            notifyDataSetChanged()
        } else {
            if (newItems.size > old.size) {
                notifyItemRangeInserted(old.size, newItems.size - old.size)
            }
            newItems.forEachIndexed { i, it ->
                if (i < old.size && it != old[i]) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder =
        RecommendViewHolder.from(
            parent,
            quantityChangeListener,
            lifecycleOwner,
        )

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])

        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = screenWidth * 0.4f

        val params = holder.itemView.layoutParams
        if (params != null) {
            params.width = itemWidth.toInt()
            holder.itemView.layoutParams = params
        }
    }

    override fun getItemCount(): Int = items.size
}
