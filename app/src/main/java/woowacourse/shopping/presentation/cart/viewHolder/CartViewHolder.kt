package woowacourse.shopping.presentation.cart.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.CartItemBinding
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartViewHolder(
    parent: ViewGroup,
    cartHandler: CartEventHandler,
    quantityHandler: ProductQuantityHandler,
) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false),
    ) {
    private val binding: CartItemBinding = CartItemBinding.bind(itemView)

    init {
        binding.cartHandler = cartHandler
        binding.quantityHandler = quantityHandler
    }

    fun bind(cartProduct: ProductUiModel) {
        binding.product = cartProduct
        binding.executePendingBindings()
    }
}
