package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.CartListener
import woowacourse.shopping.ui.cart.CartUiModel
import woowacourse.shopping.ui.utils.AddCartQuantityBundle

class CartViewHolder(private val binding: ItemCartBinding) : ViewHolder(binding.root) {
    fun bind(
        cartUiModel: CartUiModel,
        cartListener: CartListener,
    ) {
        binding.cartUiModel = cartUiModel
        binding.ivCartExit.setOnClickListener {
            cartListener.deleteCartItem(cartUiModel.productId)
        }
        binding.addCartQuantityBundle =
            AddCartQuantityBundle(
                cartUiModel.productId,
                cartUiModel.quantity,
                cartListener::increaseQuantity,
                cartListener::decreaseQuantity,
            )
        binding.checkBoxCart.setOnCheckedChangeListener { _, isChecked ->
            cartListener.selectCartItem(cartUiModel.productId, isChecked)
        }
    }
}
