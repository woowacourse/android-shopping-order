package woowacourse.shopping.view.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartProductBinding

class CartProductViewHolder(
    private val binding: ItemShoppingCartProductBinding,
    onRemoveProduct: (cartItemId: Long) -> Unit,
    private val onSelect: (productItem: CartItemType.ProductItem) -> Unit,
    private val onUnselect: (productItem: CartItemType.ProductItem) -> Unit,
    private val onPlusQuantity: (productItem: CartItemType.ProductItem) -> Unit,
    private val onMinusQuantity: (productItem: CartItemType.ProductItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.shoppingCartItemActionListener =
            object : CartItemActionListener {
                override fun onRemoveProduct(item: CartItemType.ProductItem) {
                    onRemoveProduct(item.cartItem.id)
                }

                override fun onPlusProductQuantity(item: CartItemType.ProductItem) {
                    onPlusQuantity(item)
                }

                override fun onMinusProductQuantity(item: CartItemType.ProductItem) {
                    onMinusQuantity(item)
                }
            }
    }

    fun bind(item: CartItemType.ProductItem) {
        binding.productItem = item
        binding.shoppingCartProductCheckBox.setOnClickListener {
            if (binding.shoppingCartProductCheckBox.isChecked) {
                onSelect(item)
            } else {
                onUnselect(item)
            }
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRemoveProduct: (cartItemId: Long) -> Unit,
            onSelect: (productItem: CartItemType.ProductItem) -> Unit,
            onUnselect: (productItem: CartItemType.ProductItem) -> Unit,
            onPlusQuantity: (CartItemType.ProductItem) -> Unit,
            onMinusQuantity: (CartItemType.ProductItem) -> Unit,
        ): CartProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemShoppingCartProductBinding.inflate(layoutInflater, parent, false)
            return CartProductViewHolder(
                binding,
                onRemoveProduct,
                onSelect,
                onUnselect,
                onPlusQuantity,
                onMinusQuantity,
            )
        }
    }
}
