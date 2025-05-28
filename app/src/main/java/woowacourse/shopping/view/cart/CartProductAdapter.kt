package woowacourse.shopping.view.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CartProductAdapter(
    private val onRemoveProduct: (cartItemId: Long) -> Unit,
    private val onCartPaginationListener: OnCartPaginationListener,
    private val onSelect: (cartItemId: Long) -> Unit,
    private val onUnselect: (cartItemId: Long) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<CartItemType> = emptyList()

    override fun getItemViewType(position: Int): Int = items[position].viewType.ordinal

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
            is CartProductViewHolder -> holder.bind(items[position] as CartItemType.ProductItem)
            is CartPaginationViewHolder -> holder.bind(items[position] as CartItemType.PaginationItem)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(items: List<CartItemType>) {
        val isProductItemEmpty = items.size == 1
        if (isProductItemEmpty) {
            onCartPaginationListener.onMinusPage()
        }

        val oldItems = this.items.size
        notifyItemRangeRemoved(0, oldItems - 1)

        this.items = items
        notifyItemRangeInserted(0, items.size - 1)

        val paginationItemPosition = itemCount - 1
        notifyItemChanged(paginationItemPosition)
    }
}
