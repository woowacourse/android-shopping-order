package woowacourse.shopping.view.shoppingCart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ShoppingCartProductAdapter(
    private val shoppingCartListener: ShoppingCartListener,
) : ListAdapter<ShoppingCartItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<ShoppingCartItem>() {
            override fun areItemsTheSame(
                oldItem: ShoppingCartItem,
                newItem: ShoppingCartItem,
            ): Boolean =
                when {
                    oldItem is ShoppingCartItem.ShoppingCartProductItem && newItem is ShoppingCartItem.ShoppingCartProductItem ->
                        oldItem.shoppingCartProduct.id == newItem.shoppingCartProduct.id

                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: ShoppingCartItem,
                newItem: ShoppingCartItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val viewType: ShoppingCartItem.ItemType = ShoppingCartItem.ItemType.from(viewType)
        return when (viewType) {
            ShoppingCartItem.ItemType.PRODUCT ->
                ShoppingCartProductViewHolder.of(
                    parent,
                    shoppingCartListener,
                )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ShoppingCartProductViewHolder -> holder.bind(getItem(position) as ShoppingCartItem.ShoppingCartProductItem)
        }
    }

    interface ShoppingCartListener :
        ShoppingCartProductViewHolder.ShoppingCartProductClickListener
}
