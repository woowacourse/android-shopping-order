package woowacourse.shopping.presentation.view.order.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.view.order.cart.event.CartStateListener

class CartViewHolder(
    private val binding: ItemCartBinding,
    eventListener: CartStateListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
    }

    fun bind(cartItem: DisplayModel<CartProductUiModel>) {
        binding.cartItem = cartItem.data
        binding.isSelected = cartItem.isSelected
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CartStateListener,
        ): CartViewHolder {
            val binding =
                ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CartViewHolder(binding, eventListener)
        }
    }
}
