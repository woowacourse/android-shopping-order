package woowacourse.shopping.ui.cart.cartitem.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel

class CartViewHolder(
    val binding: ItemCartBinding,
    private val viewModel: CartViewModel,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(cartUiModel: CartUiModel) {
        binding.cartUiModel = cartUiModel
        binding.vm = viewModel
    }
}
