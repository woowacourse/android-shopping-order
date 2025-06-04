package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Cart

class CartAdapter(
    private val cartClickListener: CartViewHolder.CartClickListener,
) : ListAdapter<Cart, CartViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, cartClickListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        val item: Cart = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Cart>() {
                override fun areItemsTheSame(
                    oldItem: Cart,
                    newItem: Cart,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Cart,
                    newItem: Cart,
                ): Boolean = oldItem == newItem
            }
    }
}
