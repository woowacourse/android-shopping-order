package woowacourse.shopping.feature.cart.cartdetail.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem

class CartViewHolder(
    val binding: ItemCartBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
    }
}
