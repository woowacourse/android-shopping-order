package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.CustomCartQuantity

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val cartClickListener: CartClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.cartClickListener = cartClickListener
    }

    fun bind(cart: CartGoodsItem) {
        binding.cart = cart
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    cartClickListener.addToCart(cart.cart)
                }

                override fun onRemoveClick() {
                    cartClickListener.removeFromCart(cart.cart)
                }
            },
        )
    }

    companion object {
        fun from(
            parent: ViewGroup,
            cartClickListener: CartClickListener,
        ): CartViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCartBinding.inflate(layoutInflater, parent, false)
            return CartViewHolder(binding, cartClickListener)
        }
    }

    interface CartClickListener {
        fun onClickDeleteButton(cart: Cart)

        fun addToCart(cart: Cart)

        fun removeFromCart(cart: Cart)

        fun toggleCheckedItem(cart: Cart)
    }
}
