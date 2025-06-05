package woowacourse.shopping.view.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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
) : ListAdapter<ProductsItem, RecyclerView.ViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int = currentList[position].viewType.ordinal

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
            is RecentViewedProductsViewHolder -> holder.bind(currentList[position] as ProductsItem.RecentViewedProductsItem)
            is ProductViewHolder -> holder.bind(currentList[position] as ProductsItem.ProductItem)
            is ProductMoreViewHolder -> holder.bind(currentList[position] as ProductsItem.LoadItem)
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<ProductsItem>() {
                override fun areItemsTheSame(
                    oldItem: ProductsItem,
                    newItem: ProductsItem,
                ): Boolean =
                    when (oldItem) {
                        is ProductsItem.RecentViewedProductsItem -> newItem is ProductsItem.RecentViewedProductsItem
                        is ProductsItem.ProductItem -> newItem is ProductsItem.ProductItem && oldItem.product.id == newItem.product.id
                        is ProductsItem.LoadItem -> newItem is ProductsItem.LoadItem
                    }

                override fun areContentsTheSame(
                    oldItem: ProductsItem,
                    newItem: ProductsItem,
                ): Boolean = oldItem == newItem
            }
    }
}
