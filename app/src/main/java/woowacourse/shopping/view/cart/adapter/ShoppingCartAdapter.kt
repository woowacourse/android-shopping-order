package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.OnClickShoppingCart
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartViewHolder
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartAdapter(
    private val onClickShoppingCart: OnClickShoppingCart,
    private val onClickCartItemCounter: OnClickCartItemCounter,
) : ListAdapter<CartItem, ShoppingCartViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingCartViewHolder {
        val binding = ItemShoppingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingCartViewHolder(binding, onClickCartItemCounter, onClickShoppingCart)
    }

    override fun onBindViewHolder(
        holder: ShoppingCartViewHolder,
        position: Int,
    ) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CartItem>() {
                override fun areItemsTheSame(
                    oldItem: CartItem,
                    newItem: CartItem,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CartItem,
                    newItem: CartItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
