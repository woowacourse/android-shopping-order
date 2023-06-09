package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.CartListEvent
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartListViewHolder private constructor(
    private val binding: ItemCartBinding,
    cartListEvent: CartListEvent
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.cartListEvent = cartListEvent
        binding.counter.binding.counterEvent = cartListEvent
    }

    fun bind(cartItem: CartItemUIState) {
        binding.cartItem = cartItem
    }

    companion object {
        fun from(
            parent: ViewGroup,
            cartListEvent: CartListEvent
        ): CartListViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            val binding = ItemCartBinding.bind(view)
            return CartListViewHolder(
                binding, cartListEvent
            )
        }
    }
}
