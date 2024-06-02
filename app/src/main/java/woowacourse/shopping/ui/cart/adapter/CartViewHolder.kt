package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartItemClickListener
import woowacourse.shopping.ui.cart.CartUiModel

class CartViewHolder(
    val binding: ItemCartBinding,
    private val cartItemClickListener: CartItemClickListener,
    private val countButtonClickListener: CountButtonClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(cartUiModel: CartUiModel) {
        binding.cartUiModel = cartUiModel
        binding.cartItemClickListener = cartItemClickListener
        binding.countButtonClickListener = countButtonClickListener
    }
}
