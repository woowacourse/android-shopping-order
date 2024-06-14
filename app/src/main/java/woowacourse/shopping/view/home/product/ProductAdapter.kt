package woowacourse.shopping.view.home.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductPlaceholderBinding
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.home.HomeEventListener
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.PRODUCT_PLACEHOLDER_VIEW_TYPE
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.PRODUCT_VIEW_TYPE
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem

class ProductAdapter(
    private val homeClickListener: HomeEventListener,
    private val quantityClickListener: QuantityEventListener,
) : ListAdapter<HomeViewItem, RecyclerView.ViewHolder>(ProductDiffUtil) {
    init {
        submitList(List(6) { HomeViewItem.ProductPlaceHolderViewItem() })
    }

    override fun getItemViewType(position: Int): Int {
        if (position == currentList.size) {
            return 2
        }
        return currentList[position].viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PRODUCT_VIEW_TYPE -> {
                ProductViewHolder(ItemProductBinding.inflate(inflater, parent, false))
            }
            PRODUCT_PLACEHOLDER_VIEW_TYPE -> {
                ProductPlaceholderViewHolder(
                    ItemProductPlaceholderBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                )
            }
            else -> {
                LoadMoreButtonViewHolder(ItemLoadMoreButtonBinding.inflate(inflater, parent, false))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (position == currentList.size) {
            if (holder is LoadMoreButtonViewHolder) {
                holder.bind(homeClickListener)
            }
            return
        }
        val shoppingItem = currentList[position]
        if (holder is ProductViewHolder && shoppingItem is ProductViewItem) {
            holder.bind(shoppingItem, homeClickListener, quantityClickListener)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    fun loadData(productItems: List<ProductViewItem>) {
        if (isFirstLoad()) submitList(null)
        submitList(productItems)
    }

    private fun isFirstLoad() = currentList.all { it.viewType == PRODUCT_PLACEHOLDER_VIEW_TYPE }
}
