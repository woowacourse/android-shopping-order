package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.feature.CustomCartQuantity

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val cartClickListener: CartClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.cartClickListener = cartClickListener
    }

    fun bind(cart: CartProduct) {
        binding.cart = cart
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    cartClickListener.addToCart(cart)
                }

                override fun onRemoveClick() {
                    cartClickListener.removeFromCart(cart)
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
        fun onClickDeleteButton(cart: CartProduct)

        fun addToCart(cart: CartProduct)

        fun removeFromCart(cart: CartProduct)

        fun toggleCheckedItem(cart: CartProduct)
    }
}
