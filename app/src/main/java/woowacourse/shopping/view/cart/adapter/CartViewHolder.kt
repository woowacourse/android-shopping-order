package woowacourse.shopping.view.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.view.cart.viewmodel.CartViewModel

class CartViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartViewItem: ShoppingCartViewItem.CartViewItem,
        viewModel: CartViewModel,
    ) {
        binding.cartViewItem = cartViewItem
        binding.viewModel = viewModel
    }
}
