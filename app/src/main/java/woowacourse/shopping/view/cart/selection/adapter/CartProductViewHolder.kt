package woowacourse.shopping.view.cart.selection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartProductBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.view.util.QuantityControlEventHandler

class CartProductViewHolder(
    private val binding: ItemCartProductBinding,
    eventHandler: EventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currentItem: CartProduct

    init {
        binding.handler = eventHandler
        binding.itemQuantityControl.tvIncrease.setOnClickListener {
            eventHandler.onQuantityIncreaseClick(currentItem)
        }
        binding.itemQuantityControl.tvDecrease.setOnClickListener {
            eventHandler.onQuantityDecreaseClick(currentItem)
        }
    }

    fun bind(cartProductItem: CartProductItem) {
        currentItem = cartProductItem.cartProduct
        binding.cartProduct = cartProductItem.cartProduct
        binding.isSelected = cartProductItem.isSelected
    }

    interface EventHandler : QuantityControlEventHandler<CartProduct> {
        fun onProductRemoveClick(item: CartProduct)

        fun onSelectItem(item: CartProduct)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: EventHandler,
        ): CartProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCartProductBinding.inflate(inflater, parent, false)
            return CartProductViewHolder(binding, eventListener)
        }
    }
}
