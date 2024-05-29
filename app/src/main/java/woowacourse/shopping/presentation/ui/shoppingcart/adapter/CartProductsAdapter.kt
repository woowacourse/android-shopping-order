package woowacourse.shopping.presentation.ui.shoppingcart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartProductBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.shoppingcart.ShoppingCartActionHandler

class CartProductsAdapter(
    private val actionHandler: ShoppingCartActionHandler,
    private val productCountHandler: ProductCountHandler,
) : ListAdapter<Cart, CartProductsAdapter.CartProductViewHolder>(CartDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HolderCartProductBinding.inflate(inflater, parent, false)
        return CartProductViewHolder(binding, actionHandler, productCountHandler)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), position)
    }

    class CartProductViewHolder(
        private val binding: HolderCartProductBinding,
        private val actionHandler: ShoppingCartActionHandler,
        private val productCountHandler: ProductCountHandler,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cart: Cart,
            position: Int,
        ) {
            binding.apply {
                this.cart = cart
                this@apply.actionHandler = this@CartProductViewHolder.actionHandler
                this@apply.position = position
                this@apply.productCountHandler = this@CartProductViewHolder.productCountHandler
                executePendingBindings()
            }
        }
    }

    object CartDiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
