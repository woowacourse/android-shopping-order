package woowacourse.shopping.view.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.CartItemClickListener
import woowacourse.shopping.view.cart.QuantityClickListener

class CartViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartViewItem: ShoppingCartViewItem.CartViewItem,
        cartItemClickListener: CartItemClickListener,
        quantityClickListener: QuantityClickListener,
    ) {
        binding.cartViewItem = cartViewItem
        binding.cartItemClickListener = cartItemClickListener
        binding.quantityClickListener = quantityClickListener
    }
}
