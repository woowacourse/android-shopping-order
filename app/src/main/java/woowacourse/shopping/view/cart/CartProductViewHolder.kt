package woowacourse.shopping.view.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding
import woowacourse.shopping.domain.product.CartItem

class CartProductViewHolder(
    private val binding: ItemShoppingCartProductBinding,
    onRemoveProduct: (cartItem: CartItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.shoppingCartItemActionListener =
            object : CartItemActionListener {
                override fun onRemoveProduct(item: woowacourse.shopping.view.cart.CartItemType.ProductItem) {
                    onRemoveProduct(item.cartItem)
                }

                override fun onPlusProductQuantity(item: woowacourse.shopping.view.cart.CartItemType.ProductItem) {
                    item.quantity++
                    binding.invalidateAll()
                }

                override fun onMinusProductQuantity(item: woowacourse.shopping.view.cart.CartItemType.ProductItem) {
                    if (item.quantity == 1) {
                        onRemoveProduct(item)
                        return
                    }

                    item.quantity--
                    binding.invalidateAll()
                }
            }
    }

    fun bind(item: woowacourse.shopping.view.cart.CartItemType.ProductItem) {
        binding.productItem = item
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRemoveProduct: (CartItem) -> Unit,
        ): CartProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemShoppingCartProductBinding.inflate(layoutInflater, parent, false)
            return CartProductViewHolder(binding, onRemoveProduct)
        }
    }
}
