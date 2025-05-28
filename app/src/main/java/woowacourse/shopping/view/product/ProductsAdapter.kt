package woowacourse.shopping.view.product

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.product.Product

class ProductsAdapter(
    private val onSelectProduct: (Product) -> Unit,
    private val onLoad: () -> Unit,
    private val onPlusQuantity: (
        productId: Long,
        quantity: Int,
    ) -> Unit,
    private val onMinusQuantity: (
        productId: Long,
        quantity: Int,
    ) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<ProductsItem> = emptyList()

    override fun getItemViewType(position: Int): Int = items[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ProductsItem.ItemType.from(viewType)) {
            ProductsItem.ItemType.RECENT_VIEWED_PRODUCT ->
                RecentViewedProductsViewHolder.of(parent, onSelectProduct)

            ProductsItem.ItemType.PRODUCT ->
                ProductViewHolder.of(parent, onSelectProduct, onPlusQuantity, onMinusQuantity)

            ProductsItem.ItemType.MORE ->
                ProductMoreViewHolder.of(parent, onLoad)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is RecentViewedProductsViewHolder -> holder.bind(items[position] as ProductsItem.RecentViewedProductsItem)
            is ProductViewHolder -> holder.bind(items[position] as ProductsItem.ProductItem)
            is ProductMoreViewHolder -> holder.bind(items[position] as ProductsItem.LoadItem)
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(items: List<ProductsItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
