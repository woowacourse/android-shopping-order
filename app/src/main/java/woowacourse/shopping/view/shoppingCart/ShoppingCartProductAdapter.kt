package woowacourse.shopping.view.shoppingCart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class ShoppingCartProductAdapter(
    private val shoppingCartListener: ShoppingCartListener,
) : ListAdapter<ShoppingCartItem, ShoppingCartProductViewHolder>(
        object : DiffUtil.ItemCallback<ShoppingCartItem>() {
            override fun areItemsTheSame(
                oldItem: ShoppingCartItem,
                newItem: ShoppingCartItem,
            ): Boolean = oldItem.shoppingCartProduct.id == newItem.shoppingCartProduct.id

            override fun areContentsTheSame(
                oldItem: ShoppingCartItem,
                newItem: ShoppingCartItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingCartProductViewHolder =
        ShoppingCartProductViewHolder.of(
            parent,
            shoppingCartListener,
        )

    override fun onBindViewHolder(
        holder: ShoppingCartProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    interface ShoppingCartListener : ShoppingCartProductViewHolder.ShoppingCartProductClickListener
}
