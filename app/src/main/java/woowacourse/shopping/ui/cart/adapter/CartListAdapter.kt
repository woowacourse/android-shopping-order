package woowacourse.shopping.ui.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.cart.CartListEvent
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartListAdapter(
    private val cartItems: MutableList<CartItemUIState> = mutableListOf(),
    private val cartListEvent: CartListEvent
) : RecyclerView.Adapter<CartListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder.from(
            parent, cartListEvent
        )
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    fun setCartItems(cartItems: List<CartItemUIState>) {
        this.cartItems.clear()
        this.cartItems.addAll(cartItems)
        notifyDataSetChanged()
    }
}
