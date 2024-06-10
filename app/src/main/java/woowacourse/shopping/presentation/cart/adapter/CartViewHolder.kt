package woowacourse.shopping.presentation.cart.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.cart.CartActionHandler
import woowacourse.shopping.presentation.cart.model.CartUiModel

class CartViewHolder(private val binding: ItemCartBinding) : ViewHolder(binding.root) {
    fun bind(
        cartUiModel: CartUiModel,
        cartActionHandler: CartActionHandler,
    ) {
        binding.cartUiModel = cartUiModel
        binding.actionHandler = cartActionHandler
    }
}
