package woowacourse.shopping.view.cart.carts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.view.cart.state.CartState
import woowacourse.shopping.view.core.handler.CartQuantityHandler

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val handler: Handler,
    private val cartQuantityHandler: CartQuantityHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CartState) {
        with(binding) {
            model = item
            eventHandler = handler
            cartQuantityEventHandler = cartQuantityHandler
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            handler: Handler,
            cartQuantityHandler: CartQuantityHandler,
        ): CartViewHolder {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            val binding = ItemCartBinding.bind(inflater)
            return CartViewHolder(binding, handler, cartQuantityHandler)
        }
    }

    interface Handler {
        fun onClickDeleteItem(cartId: Long)

        fun onCheckedChanged(
            cartId: Long,
            isChecked: Boolean,
        )
    }
}
