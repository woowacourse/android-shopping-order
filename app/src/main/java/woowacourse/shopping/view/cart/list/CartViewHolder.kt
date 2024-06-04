package woowacourse.shopping.view.cart.list

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.view.cart.QuantityEventListener

class CartViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartViewItem: ShoppingCartViewItem.CartViewItem,
        cartItemClickListener: CartItemClickListener,
        quantityClickListener: QuantityEventListener,
    ) {
        binding.cartViewItem = cartViewItem
        binding.cartItemClickListener = cartItemClickListener
        binding.quantityClickListener = quantityClickListener
        binding.checkboxCart.isSelected = cartViewItem.isSelected
    }
}
