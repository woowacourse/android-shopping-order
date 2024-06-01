package woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartProductBinding
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.CartProduct
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.CartSelectActionHandler

class CartProductsAdapter(
    private val actionHandler: CartSelectActionHandler,
    private val productCountHandler: ProductCountHandler,
) : ListAdapter<CartProduct, CartProductsAdapter.CartProductViewHolder>(CartProductDiffCallback) {
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
        private val actionHandler: CartSelectActionHandler,
        private val productCountHandler: ProductCountHandler,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cartProduct: CartProduct,
            position: Int,
        ) {
            binding.apply {
                this.cartProduct = cartProduct
                this@apply.actionHandler = this@CartProductViewHolder.actionHandler
                this@apply.position = position
                this@apply.productCountHandler = this@CartProductViewHolder.productCountHandler
                executePendingBindings()
            }
        }
    }

    object CartProductDiffCallback : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct,
        ): Boolean {
            return oldItem.cart.id == newItem.cart.id
        }

        override fun areContentsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
