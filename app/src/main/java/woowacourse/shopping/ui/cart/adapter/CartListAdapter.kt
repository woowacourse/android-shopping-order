package woowacourse.shopping.ui.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.cart.CartListEvent
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartListAdapter(
    private val cartListEvent: CartListEvent
) : ListAdapter<CartItemUIState, CartListViewHolder>(CartListAdapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder.from(
            parent, cartListEvent
        )
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setCartItems(cartItems: List<CartItemUIState>) {
        submitList(cartItems)
    }
}
