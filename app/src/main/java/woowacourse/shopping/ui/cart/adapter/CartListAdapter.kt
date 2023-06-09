package woowacourse.shopping.ui.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartListAdapter(
    private val onClickCloseButton: (Long) -> Unit,
    private val onClickCheckBox: (Long, Boolean) -> Unit,
    private val onClickPlus: (Long) -> Unit,
    private val onClickMinus: (Long) -> Unit
) : ListAdapter<CartItemUIState, CartListViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder.create(
            parent,
            onClickCloseButton,
            onClickCheckBox,
            onClickPlus,
            onClickMinus
        )
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setCartItems(cartItems: List<CartItemUIState>) {
        submitList(cartItems)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CartItemUIState>() {
            override fun areItemsTheSame(
                oldItem: CartItemUIState,
                newItem: CartItemUIState
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CartItemUIState,
                newItem: CartItemUIState
            ): Boolean = oldItem == newItem
        }
    }
}
