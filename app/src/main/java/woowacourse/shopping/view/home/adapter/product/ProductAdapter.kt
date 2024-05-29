package woowacourse.shopping.view.home.adapter.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductPlaceholderBinding
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.home.HomeClickListener
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.Companion.PRODUCT_PLACEHOLDER_VIEW_TYPE
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.Companion.PRODUCT_VIEW_TYPE
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.LoadMoreViewItem
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem

class ProductAdapter(
    private val homeClickListener: HomeClickListener,
    private val quantityClickListener: QuantityClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val homeViewItems: MutableList<HomeViewItem> =
        MutableList(20) { HomeViewItem.ProductPlaceHolderViewItem() }

    override fun getItemViewType(position: Int): Int {
        return homeViewItems[position].viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == PRODUCT_VIEW_TYPE) {
            ProductViewHolder(ItemProductBinding.inflate(inflater, parent, false))
        } else if (viewType == PRODUCT_PLACEHOLDER_VIEW_TYPE) {
            ProductPlaceholderViewHolder(
                ItemProductPlaceholderBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )
        } else {
            LoadMoreButtonViewHolder(ItemLoadMoreButtonBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val shoppingItem = homeViewItems[position]
        if (holder is ProductViewHolder && shoppingItem is ProductViewItem) {
            holder.bind(shoppingItem, homeClickListener, quantityClickListener)
        }
        if (holder is LoadMoreButtonViewHolder) {
            holder.bind(homeClickListener)
        }
    }

    override fun getItemCount(): Int {
        return homeViewItems.size
    }

    fun loadData(
        productItems: List<ProductViewItem>,
        canLoadMore: Boolean,
    ) {
        val currentSize =
            if (isFirstLoad()) {
                homeViewItems.clear()
                0
            } else {
                homeViewItems.size - 1
            }
        val items = productItems.subList(currentSize, productItems.size)

        homeViewItems.removeLastOrNull()
        homeViewItems += items

        if (canLoadMore) {
            homeViewItems += LoadMoreViewItem()
            notifyItemRangeInserted(homeViewItems.size, items.size + 1)
        } else {
            notifyItemRangeInserted(homeViewItems.size, items.size)
        }
    }

    fun updateData(updatedItems: List<ProductViewItem>) {
        updatedItems.forEach { updatedItem ->
            updateProductQuantity(updatedItem)
        }
    }

    fun updateProductQuantity(updatedProductItem: ProductViewItem) {
        if (!isFirstLoad()) {
            val position =
                homeViewItems.indexOfFirst { item ->
                    item is ProductViewItem && item.product.id == updatedProductItem.product.id
                }

            if (position != -1) {
                homeViewItems[position] = updatedProductItem
                notifyItemChanged(position)
            }
        }
    }

    private fun isFirstLoad() = homeViewItems.all { it.viewType == PRODUCT_PLACEHOLDER_VIEW_TYPE }
}
