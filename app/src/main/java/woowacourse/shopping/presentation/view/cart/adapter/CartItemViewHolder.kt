package woowacourse.shopping.presentation.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemEventListener

class CartItemViewHolder(
    private val binding: ItemCartBinding,
    private val eventListener: CartItemEventListener,
    itemCounterListener: ItemCounterListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
        binding.cartItemCounter.eventListener = itemCounterListener
    }

    fun bind(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
        binding.product = cartItem.cartItem.toProductUiModel()
        binding.checkbox.setOnCheckedChangeListener(null)
        binding.checkbox.isChecked = cartItem.isSelected
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            eventListener.onProductSelectionToggle(cartItem, isChecked)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CartItemEventListener,
            itemCounterListener: ItemCounterListener,
        ): CartItemViewHolder {
            val binding =
                ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CartItemViewHolder(binding, eventListener, itemCounterListener)
        }
    }
}
