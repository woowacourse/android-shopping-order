package woowacourse.shopping.ui.order.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel

class CartViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartViewItem: ShoppingCartViewItem.CartViewItem,
        viewModel: OrderViewModel,
    ) {
        binding.cartViewItem = cartViewItem
        binding.viewModel = viewModel
    }
}