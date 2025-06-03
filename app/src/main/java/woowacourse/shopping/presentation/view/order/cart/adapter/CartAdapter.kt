package woowacourse.shopping.presentation.view.order.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.view.order.cart.event.CartStateListener

class CartAdapter(
    initialProducts: List<DisplayModel<CartProductUiModel>> = emptyList(),
    private val eventListener: CartStateListener,
) : RecyclerView.Adapter<CartViewHolder>() {
    private val products = initialProducts.toMutableList()

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    fun updateItemsManually(newProducts: List<DisplayModel<CartProductUiModel>>) {
        removeMissingItems(newProducts)
        updateChangedItems(newProducts)
        addNewItems(newProducts)
    }

    private fun removeMissingItems(newProducts: List<DisplayModel<CartProductUiModel>>) {
        val iterator = products.listIterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (newProducts.none { it.data.productId == item.data.productId }) {
                val index = iterator.previousIndex()
                iterator.remove()
                notifyItemRemoved(index)
            }
        }
    }

    private fun updateChangedItems(newProducts: List<DisplayModel<CartProductUiModel>>) {
        newProducts.forEach { newItem ->
            val oldIndex = products.indexOfFirst { it.data.productId == newItem.data.productId }
            if (oldIndex != -1 && products[oldIndex] != newItem) {
                products[oldIndex] = newItem
                notifyItemChanged(oldIndex)
            }
        }
    }

    private fun addNewItems(newProducts: List<DisplayModel<CartProductUiModel>>) {
        newProducts.forEach { newItem ->
            val exists = products.any { it.data.productId == newItem.data.productId }
            if (!exists) {
                products.add(newItem)
                notifyItemInserted(itemCount - 1)
            }
        }
    }
}
