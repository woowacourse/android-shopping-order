package woowacourse.shopping.view.shoppingCart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.product.CartItem

class ShoppingCartProductAdapter(
    private val onRemoveProduct: (cartItem: CartItem) -> Unit,
    private val onShoppingCartPaginationListener: OnShoppingCartPaginationListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<ShoppingCartItem> = emptyList()

    override fun getItemViewType(position: Int): Int = items[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val viewType: ShoppingCartItem.ItemType = ShoppingCartItem.ItemType.from(viewType)
        return when (viewType) {
            ShoppingCartItem.ItemType.PRODUCT ->
                ShoppingCartProductViewHolder.of(
                    parent,
                    onRemoveProduct,
                )

            ShoppingCartItem.ItemType.PAGINATION ->
                ShoppingCartPaginationViewHolder.of(
                    parent,
                    onShoppingCartPaginationListener,
                )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ShoppingCartProductViewHolder -> holder.bind(items[position] as ShoppingCartItem.ProductItem)
            is ShoppingCartPaginationViewHolder -> holder.bind(items[position] as ShoppingCartItem.PaginationItem)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(items: List<ShoppingCartItem>) {
        val isProductItemEmpty = items.size == 1
        if (isProductItemEmpty) {
            onShoppingCartPaginationListener.onMinusPage()
        }

        val oldItems = this.items.size
        notifyItemRangeRemoved(0, oldItems - 1)

        this.items = items
        notifyItemRangeInserted(0, items.size - 1)

        val paginationItemPosition = itemCount - 1
        notifyItemChanged(paginationItemPosition)
    }
}
