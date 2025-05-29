package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener

class RecommendAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val externalQuantityChange: QuantityChangeListener,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    private val items = mutableListOf<CartItem>()

    fun setItems(newItems: List<CartItem>) {
        val old = items.toList()
        items.clear()
        items.addAll(newItems)
        when {
            old.isEmpty() -> {
                @Suppress("NotifyDataSetChanged")
                notifyDataSetChanged()
            }
            newItems.size > old.size -> {
                notifyItemRangeInserted(old.size, newItems.size - old.size)
            }
            else -> {
                newItems.forEachIndexed { idx, newItem ->
                    if (idx < old.size && newItem != old[idx]) {
                        notifyItemChanged(idx)
                    }
                }
            }
        }
    }

    private fun incrementItemQuantity(itemId: Int) {
        val pos = items.indexOfFirst { it.goods.id == itemId }
        if (pos != -1) {
            val old = items[pos]
            items[pos] = old.copy(quantity = old.quantity + 1)
            notifyItemChanged(pos)
        }
    }

    private fun decrementItemQuantity(itemId: Int) {
        val pos = items.indexOfFirst { it.goods.id == itemId }
        if (pos != -1) {
            val old = items[pos]
            items[pos] = old.copy(quantity = old.quantity - 1)
            if (items[pos].quantity < 0) items[pos].quantity = 0
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val wrappedListener =
            object : QuantityChangeListener {
                override fun onIncrease(cartItem: CartItem) {
                    externalQuantityChange.onIncrease(cartItem)
                    incrementItemQuantity(cartItem.goods.id)
                }

                override fun onDecrease(cartItem: CartItem) {
                    externalQuantityChange.onDecrease(cartItem)
                    decrementItemQuantity(cartItem.goods.id)
                }
            }
        return RecommendViewHolder.from(parent, wrappedListener, lifecycleOwner)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        val item = items[position]
        holder.bind(item)
        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
        holder.itemView.layoutParams =
            holder.itemView.layoutParams.apply {
                width = screenWidth / 3
            }
    }
}
