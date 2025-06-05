package woowacourse.shopping.view.cart.select.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.cart.select.CartProductSelectEventHandler

class CartProductAdapter(
    private val eventHandler: CartProductSelectEventHandler,
) : ListAdapter<CartProductItem, CartProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder = CartProductViewHolder.from(parent, eventHandler)

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<CartProductItem>() {
                override fun areItemsTheSame(
                    oldItem: CartProductItem,
                    newItem: CartProductItem,
                ): Boolean = oldItem.cartProduct.id == newItem.cartProduct.id

                override fun areContentsTheSame(
                    oldItem: CartProductItem,
                    newItem: CartProductItem,
                ): Boolean = oldItem == newItem
            }
    }
}
