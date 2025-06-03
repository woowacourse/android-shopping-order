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
) : ListAdapter<CartItemType, RecyclerView.ViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int = currentList[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val viewType: CartItemType.ItemType =
            CartItemType.ItemType.from(viewType)
        return when (viewType) {
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
            is CartProductViewHolder -> holder.bind(currentList[position] as CartItemType.ProductItem)
            is CartPaginationViewHolder -> holder.bind(currentList[position] as CartItemType.PaginationItem)
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<CartItemType>() {
                override fun areItemsTheSame(
                    oldItem: CartItemType,
                    newItem: CartItemType,
                ): Boolean =
                    when (oldItem) {
                        is CartItemType.PaginationItem -> newItem is CartItemType.PaginationItem
                        is CartItemType.ProductItem -> newItem is CartItemType.ProductItem && oldItem.cartItemId == newItem.cartItemId
                    }

                override fun areContentsTheSame(
                    oldItem: CartItemType,
                    newItem: CartItemType,
                ): Boolean = oldItem == newItem
            }
    }
}
