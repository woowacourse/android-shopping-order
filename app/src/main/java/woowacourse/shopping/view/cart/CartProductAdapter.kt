package woowacourse.shopping.view.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CartProductAdapter(
    private val onRemoveProduct: (cartItem: CartItemType.ProductItem) -> Unit,
    private val onCartPaginationListener: OnCartPaginationListener,
    private val onSelect: (productItem: CartItemType.ProductItem) -> Unit,
    private val onUnselect: (productItem: CartItemType.ProductItem) -> Unit,
    private val onPlusQuantity: (productItem: CartItemType.ProductItem) -> Unit,
    private val onMinusQuantity: (productItem: CartItemType.ProductItem) -> Unit,
) : ListAdapter<CartItemType, RecyclerView.ViewHolder>(CartItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (CartItemType.ItemType.from(viewType)) {
            CartItemType.ItemType.PRODUCT ->
                CartProductViewHolder.of(
                    parent,
                    onRemoveProduct,
                    onSelect,
                    onUnselect,
                    onPlusQuantity,
                    onMinusQuantity,
                )

            CartItemType.ItemType.PAGINATION ->
                CartPaginationViewHolder.of(
                    parent,
                    onCartPaginationListener,
                )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is CartProductViewHolder -> holder.bind(getItem(position) as CartItemType.ProductItem)
            is CartPaginationViewHolder -> holder.bind(getItem(position) as CartItemType.PaginationItem)
        }
    }
}

private class CartItemDiffCallback : DiffUtil.ItemCallback<CartItemType>() {

    override fun areItemsTheSame(oldItem: CartItemType, newItem: CartItemType): Boolean {
        return when {
            oldItem is CartItemType.ProductItem && newItem is CartItemType.ProductItem ->
                oldItem.cartItemId == newItem.cartItemId

            oldItem is CartItemType.PaginationItem && newItem is CartItemType.PaginationItem ->
                true

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: CartItemType, newItem: CartItemType): Boolean {
        return oldItem == newItem
    }
}
