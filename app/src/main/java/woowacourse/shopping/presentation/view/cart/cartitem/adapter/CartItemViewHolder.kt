package woowacourse.shopping.presentation.view.cart.cartitem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.view.cart.cartitem.CartItemEventHandler
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class CartItemViewHolder(
    private val binding: ItemCartBinding,
    private val cartItemEventHandler: CartItemEventHandler,
    itemCounterEventHandler: ItemCounterEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventHandler = cartItemEventHandler
        binding.cartItemCounter.eventHandler = itemCounterEventHandler
    }

    fun bind(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
        binding.product = cartItem.cartItem.toProductUiModel()
    }

    companion object {
        fun from(
            parent: ViewGroup,
            cartItemEventHandler: CartItemEventHandler,
            itemCounterListener: ItemCounterEventHandler,
        ): CartItemViewHolder {
            val binding =
                ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CartItemViewHolder(binding, cartItemEventHandler, itemCounterListener)
        }
    }
}
