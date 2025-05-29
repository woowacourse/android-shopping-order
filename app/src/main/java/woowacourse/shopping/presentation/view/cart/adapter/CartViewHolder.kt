package woowacourse.shopping.presentation.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.toUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val eventListener: CartAdapter.CartEventListener,
    itemCounterListener: ItemCounterListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
        binding.cartItemCounter.listener = itemCounterListener
    }

    fun bind(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
        binding.product = cartItem.cartItem.toUiModel()
        binding.checkbox.setOnCheckedChangeListener(null)
        binding.checkbox.isChecked = cartItem.isSelected
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            eventListener.onProductSelectionToggle(cartItem, isChecked)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CartAdapter.CartEventListener,
            itemCounterListener: ItemCounterListener,
        ): CartViewHolder {
            val binding =
                ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CartViewHolder(binding, eventListener, itemCounterListener)
        }
    }
}
