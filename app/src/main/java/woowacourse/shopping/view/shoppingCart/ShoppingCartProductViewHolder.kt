package woowacourse.shopping.view.shoppingCart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding
import woowacourse.shopping.view.common.ProductQuantityClickListener
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem.ShoppingCartProductItem

class ShoppingCartProductViewHolder(
    private val binding: ItemShoppingCartProductBinding,
    shoppingCartListener: ShoppingCartProductClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onRemoveProduct = shoppingCartListener::onRemoveButton
        binding.shoppingCartQuantityComponent.productQuantityClickListener = shoppingCartListener
    }

    fun bind(item: ShoppingCartProductItem) {
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
        fun onRemoveButton(shoppingCartProductItem: ShoppingCartProductItem)
    }
}
