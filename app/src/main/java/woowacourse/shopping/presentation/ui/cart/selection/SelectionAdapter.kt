package woowacourse.shopping.presentation.ui.cart.selection

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.ui.cart.CartItemUiModel

class SelectionAdapter(
    private val cartItemSelectionEventHandler: CartItemSelectionEventHandler,
    private val selectionCountHandler: SelectionCountHandler,
) : RecyclerView.Adapter<SelectionViewHolder>() {
    private var cartItems: List<CartItemUiModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SelectionViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SelectionViewHolder,
        position: Int,
    ) {
        val cartItem = cartItems[position]
        return holder.bind(cartItem, cartItemSelectionEventHandler, selectionCountHandler)
    }

    override fun onBindViewHolder(
        holder: SelectionViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    SelectionAdapterPayload.QUANTITY_CHANGED -> {
                        holder.onQuantityChanged(cartItems[position])
                    }

                    SelectionAdapterPayload.IS_CHECKED_CHANGED -> {
                        holder.onIsCheckedChanged(cartItems[position])
                    }

                    else -> {}
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun submitItems(newItems: List<CartItemUiModel>) {
        val hasInitialized = cartItems.isEmpty()
        cartItems = newItems
        if (hasInitialized) {
            notifyItemRangeInserted(0, newItems.size)
        }
    }

    fun updateQuantity(updatedProductIds: Set<Long>) {
        updatedProductIds.forEach { productId ->
            val updatedPosition = cartItems.indexOfFirst { it.productId == productId }
            notifyItemChanged(updatedPosition, SelectionAdapterPayload.QUANTITY_CHANGED)
        }
    }

    fun updateChecked(updatedItemIds: Set<Long>) {
        updatedItemIds.forEach { cartItemId ->
            val updatedPosition = cartItems.indexOfFirst { it.id == cartItemId }
            notifyItemChanged(updatedPosition, SelectionAdapterPayload.IS_CHECKED_CHANGED)
        }
    }

    fun deleteItem(deletedItemId: Long) {
        val deletedPosition = cartItems.indexOfFirst { it.id == deletedItemId }
        notifyItemRemoved(deletedPosition)
    }

    fun notifyItemsAdded(addedCount: Int) {
        val startPosition = cartItems.size - addedCount
        notifyItemRangeInserted(startPosition, addedCount)
    }
}
