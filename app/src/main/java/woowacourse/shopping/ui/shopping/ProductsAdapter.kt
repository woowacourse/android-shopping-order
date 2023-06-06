package woowacourse.shopping.ui.shopping

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.shopping.viewHolder.ItemViewHolder
import woowacourse.shopping.ui.shopping.viewHolder.ProductsOnClickListener
import woowacourse.shopping.ui.shopping.viewHolder.ProductsViewHolder
import woowacourse.shopping.ui.shopping.viewHolder.ReadMoreViewHolder
import woowacourse.shopping.ui.shopping.viewHolder.RecentProductsViewHolder

class ProductsAdapter(
    private var productItemTypes: MutableList<ProductsItemType>,
    private val onClickListener: ProductsOnClickListener,
    private val onReadMoreClick: () -> Unit,
) : RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            ProductsItemType.TYPE_HEADER -> RecentProductsViewHolder.from(parent, onClickListener)
            ProductsItemType.TYPE_FOOTER -> ReadMoreViewHolder.from(parent) { onReadMoreClick() }
            ProductsItemType.TYPE_ITEM -> ProductsViewHolder.from(
                parent,
                onClickListener,
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = productItemTypes.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder) {
            is RecentProductsViewHolder -> holder.bind(productItemTypes[position] as RecentProductsItem)
            is ProductsViewHolder -> holder.bind(productItemTypes[position] as ProductItemModel)
            is ReadMoreViewHolder -> return
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (productItemTypes[position]) {
            is RecentProductsItem -> ProductsItemType.TYPE_HEADER
            is ProductItemModel -> ProductsItemType.TYPE_ITEM
            is ProductReadMore -> ProductsItemType.TYPE_FOOTER
        }
    }

    fun updateData(data: List<ProductsItemType>) {
        productItemTypes.clear()
        productItemTypes.addAll(data)
        notifyItemChanged(data.size - productItemTypes.size)
    }

    fun updateItemCount(id: Long, count: Int) {
        val index = productItemTypes.indexOfFirst {
            it is ProductItemModel && it.product.id == id
        }
        if (index != -1) {
            productItemTypes[index] =
                (productItemTypes[index] as ProductItemModel).copy(count = count)
            notifyItemRangeChanged(index, 1)
        }
    }
}
