package woowacourse.shopping.view.order.adapter.cart

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.view.order.viewmodel.OrderViewModel

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
