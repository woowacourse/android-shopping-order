package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.ui.cart.listener.OnCartItemDeleteListener
import woowacourse.shopping.ui.cart.listener.OnCartItemSelectedListener
import woowacourse.shopping.ui.model.CartItemUiModel

class CartItemViewHolder(
    private val binding: HolderCartBinding,
    private val onCartItemDeleteListener: OnCartItemDeleteListener,
    private val onItemChargeListener: OnItemQuantityChangeListener,
    private val onCartItemSelectedListener: OnCartItemSelectedListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
        binding.onCartItemDeleteListener = onCartItemDeleteListener
        binding.onItemChargeListener = onItemChargeListener
        binding.onCartItemSelectedListener = onCartItemSelectedListener
    }
}
