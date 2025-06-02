package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.Product

class CartProductAdapter(
    private val onClickHandler: CartProductViewHolder.OnClickHandler,
) : RecyclerView.Adapter<CartProductViewHolder>() {
    private val items: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartProductViewHolder(binding, onClickHandler)
    }

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        val item: Product = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun submitItems(newItems: List<Product>) {
        val oldItems = items.toList()

        updateItems(newItems, oldItems)
        removeExceedingItems(oldItems, newItems)
    }

    private fun updateItems(
        newItems: List<Product>,
        oldItems: List<Product>,
    ) {
        for ((position, newItem) in newItems.withIndex()) {
            val oldItem = oldItems.getOrNull(position)

            when {
                oldItem == null -> addItem(newItem)
                !isContentTheSame(oldItem, newItem) -> replaceItem(position, newItem)
            }
        }
    }

    private fun isContentTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem == newItem

    private fun replaceItem(
        position: Int,
        newItem: Product,
    ) {
        items[position] = newItem
        notifyItemChanged(position)
    }

    private fun addItem(item: Product) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    private fun removeExceedingItems(
        oldItems: List<Product>,
        newItems: List<Product>,
    ) {
        if (oldItems.size > newItems.size) {
            for (position in oldItems.lastIndex downTo newItems.size) {
                removeItem(position)
            }
        }
    }

    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
