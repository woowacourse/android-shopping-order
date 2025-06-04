package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.CartProduct

class CartAdapter(
    private val cartClickListener: CartViewHolder.CartClickListener,
) : ListAdapter<CartProduct, CartViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, cartClickListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        val item: CartProduct = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CartProduct>() {
                override fun areItemsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean = oldItem == newItem
            }
    }
}
