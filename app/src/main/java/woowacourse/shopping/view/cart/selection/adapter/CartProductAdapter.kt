package woowacourse.shopping.view.cart.selection.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.cart.selection.CartProductSelectionEventHandler

class CartProductAdapter(
    items: List<CartProductItem> = emptyList(),
    private val eventHandler: CartProductSelectionEventHandler,
) : RecyclerView.Adapter<CartProductViewHolder>() {
    private val items = items.toMutableList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder = CartProductViewHolder.from(parent, eventHandler)

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartProductItem>) {
        val oldSize = items.size
        val newSize = newItems.size
        val minSize = minOf(oldSize, newSize)

        for (i in 0 until minSize) {
            val oldItem = items[i]
            val newItem = newItems[i]

            if (oldItem != newItem) {
                items[i] = newItem
                notifyItemChanged(i)
            }
        }

        if (newSize > oldSize) {
            val addedItems = newItems.subList(oldSize, newSize)
            items.addAll(addedItems)
            notifyItemRangeInserted(oldSize, addedItems.size)
        } else if (oldSize > newSize) {
            for (i in oldSize - 1 downTo newSize) {
                items.removeAt(i)
            }
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }
}
