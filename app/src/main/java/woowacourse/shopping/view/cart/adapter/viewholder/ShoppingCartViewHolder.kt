package woowacourse.shopping.view.cart.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.view.cart.OnClickNavigateShoppingCart
import woowacourse.shopping.view.cart.OnClickShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartViewHolder(
    private val binding: ItemShoppingCartBinding,
    private val onClickCartItemCounter: OnClickCartItemCounter,
    private val onClickShoppingCart: OnClickShoppingCart,
    private val onClickNavigateShoppingCart: OnClickNavigateShoppingCart,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        cartItem.product.updateItemSelector(true)
        binding.cartItem = cartItem
        binding.onClickCartItemCounter = onClickCartItemCounter
        binding.onClickShoppingCart = onClickShoppingCart
        binding.onClickNavigateShoppingCart = onClickNavigateShoppingCart
    }
}
