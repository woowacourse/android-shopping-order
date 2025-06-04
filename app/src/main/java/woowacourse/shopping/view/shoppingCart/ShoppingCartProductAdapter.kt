package woowacourse.shopping.view.shoppingCart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.product.Product

class ShoppingCartProductAdapter(
    private val onRemoveProduct: (product: Product) -> Unit,
    private val shoppingCartListener: ShoppingCartListener,
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
                    shoppingCartListener,
                )

            ShoppingCartItem.ItemType.PAGINATION ->
                ShoppingCartPaginationViewHolder.of(
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
            is ShoppingCartProductViewHolder -> holder.bind(items[position] as ShoppingCartItem.ProductItem)
            is ShoppingCartPaginationViewHolder -> holder.bind(items[position] as ShoppingCartItem.PaginationItem)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(items: List<ShoppingCartItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    interface ShoppingCartListener :
        ShoppingCartPaginationViewHolder.ShoppingCartPaginationClickListener,
        ShoppingCartProductViewHolder.ShoppingCartProductClickListener
}
