package woowacourse.shopping.view.shoppingCart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ShoppingCartProductAdapter(
    private val shoppingCartListener: ShoppingCartListener,
) : ListAdapter<ShoppingCartItem.ShoppingCartProductItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<ShoppingCartItem.ShoppingCartProductItem>() {
            override fun areItemsTheSame(
                oldItem: ShoppingCartItem.ShoppingCartProductItem,
                newItem: ShoppingCartItem.ShoppingCartProductItem,
            ): Boolean = oldItem.shoppingCartProduct.id == newItem.shoppingCartProduct.id

            override fun areContentsTheSame(
                oldItem: ShoppingCartItem.ShoppingCartProductItem,
                newItem: ShoppingCartItem.ShoppingCartProductItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return ShoppingCartProductViewHolder.of(
            parent,
            shoppingCartListener,
        )
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
