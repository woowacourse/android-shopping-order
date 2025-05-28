package woowacourse.shopping.view.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding

class CartProductViewHolder(
    private val binding: ItemShoppingCartProductBinding,
    onRemoveProduct: (cartItemId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.shoppingCartItemActionListener =
            object : CartItemActionListener {
                override fun onRemoveProduct(item: CartItemType.ProductItem) {
                    onRemoveProduct(item.cartItem.id)
                }

                override fun onPlusProductQuantity(item: CartItemType.ProductItem) {
                    item.quantity++
                    binding.invalidateAll()
                }

                override fun onMinusProductQuantity(item: CartItemType.ProductItem) {
                    if (item.quantity == 1) {
                        onRemoveProduct(item)
                        return
                    }

                    item.quantity--
                    binding.invalidateAll()
                }
            }
    }

    fun bind(item: CartItemType.ProductItem) {
        binding.productItem = item
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRemoveProduct: (cartItemId: Long) -> Unit,
        ): CartProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemShoppingCartProductBinding.inflate(layoutInflater, parent, false)
            return CartProductViewHolder(binding, onRemoveProduct)
        }
    }
}
