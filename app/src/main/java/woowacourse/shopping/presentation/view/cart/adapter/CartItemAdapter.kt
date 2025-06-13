package woowacourse.shopping.presentation.view.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemEventListener

class CartItemAdapter(
    cartItems: List<CartItemUiModel> = emptyList(),
    private val eventListener: CartItemEventListener,
    private val itemCounterListener: ItemCounterListener,
) : RecyclerView.Adapter<CartItemViewHolder>() {
    private val cartItems = cartItems.toMutableList()

    override fun getItemCount(): Int = cartItems.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartItemViewHolder = CartItemViewHolder.from(parent, eventListener, itemCounterListener)

    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        position: Int,
    ) {
        holder.bind(cartItems[position])
    }

    fun updateCartItems(cartItems: List<CartItemUiModel>) {
        this.cartItems.clear()
        this.cartItems.addAll(cartItems)
        notifyDataSetChanged()
    }

    fun updateItem(updatedItem: CartItemUiModel) {
        val index = cartItems.indexOfFirst { it.cartId == updatedItem.cartId }
        if (index != -1) {
            cartItems[index] = updatedItem
            notifyItemChanged(index)
        }
    }

    fun removeProduct(id: Long) {
        val index = cartItems.indexOfFirst { it.cartId == id }
        cartItems.removeAt(index)
        notifyItemRemoved(index)
    }
}
