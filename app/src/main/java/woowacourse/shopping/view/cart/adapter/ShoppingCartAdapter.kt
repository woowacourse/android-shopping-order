package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.view.cart.OnClickNavigateShoppingCart
import woowacourse.shopping.view.cart.OnClickShoppingCart
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartViewHolder
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartAdapter(
    private val onClickShoppingCart: OnClickShoppingCart,
    private val onClickCartItemCounter: OnClickCartItemCounter,
    private val onClickNavigateShoppingCart: OnClickNavigateShoppingCart,
) : ListAdapter<CartItem, ShoppingCartViewHolder>(ShoppingCartDiffCallback()) {
    class ShoppingCartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(
            oldItem: CartItem,
            newItem: CartItem,
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: CartItem,
            newItem: CartItem,
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingCartViewHolder {
        val view =
            ItemShoppingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingCartViewHolder(
            view,
            onClickCartItemCounter,
            onClickShoppingCart,
            onClickNavigateShoppingCart,
        )
    }

    override fun onBindViewHolder(
        holder: ShoppingCartViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun updateCartItems(addedCartItems: List<CartItem>) {
        submitList(addedCartItems)
    }

    fun updateCartItem(productId: Long) {
        val position =
            currentList.indexOfFirst {
                it.product.id == productId
            }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }
}
