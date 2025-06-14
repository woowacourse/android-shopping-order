package woowacourse.shopping.view.shoppingCart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding
import woowacourse.shopping.view.common.ProductQuantityClickListener

class ShoppingCartProductViewHolder(
    private val binding: ItemShoppingCartProductBinding,
    shoppingCartListener: ShoppingCartProductClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.shoppingCartProductClickListener = shoppingCartListener
    }

    fun bind(item: ShoppingCartItem) {
        binding.shoppingCartProductItem = item
        binding.shoppingCartQuantityComponent.quantity = item.shoppingCartProduct.quantity
    }

    companion object {
        fun of(
            parent: ViewGroup,
            shoppingCartListener: ShoppingCartProductClickListener,
        ): ShoppingCartProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemShoppingCartProductBinding.inflate(layoutInflater, parent, false)
            return ShoppingCartProductViewHolder(binding, shoppingCartListener)
        }
    }

    interface ShoppingCartProductClickListener : ProductQuantityClickListener {
        fun onRemoveButton(shoppingCartProductItem: ShoppingCartItem)

        fun onProductSelectedButton(
            shoppingCartProductItem: ShoppingCartItem,
            isSelected: Boolean,
        )
    }
}
