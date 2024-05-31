package woowacourse.shopping.view.home.adapter.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductPlaceholderBinding
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.home.HomeClickListener
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.Companion.PRODUCT_PLACEHOLDER_VIEW_TYPE
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.Companion.PRODUCT_VIEW_TYPE
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.LoadMoreViewItem
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductPlaceHolderViewItem
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem

class ProductAdapter(
    private val homeClickListener: HomeClickListener,
    private val quantityClickListener: QuantityClickListener,
) : ListAdapter<HomeViewItem, RecyclerView.ViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PRODUCT_PLACEHOLDER_VIEW_TYPE -> {
                ProductPlaceholderViewHolder(
                    ItemProductPlaceholderBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                )
            }

            PRODUCT_VIEW_TYPE -> {
                ProductViewHolder(ItemProductBinding.inflate(inflater, parent, false))
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
        val shoppingItem = currentList[position]
        if (holder is ProductViewHolder && shoppingItem is ProductViewItem) {
            holder.bind(shoppingItem, homeClickListener, quantityClickListener)
        }
        if (holder is LoadMoreButtonViewHolder) {
            holder.bind(homeClickListener)
        }
    }

    fun submitProductItems(productItems: List<ProductViewItem>, canLoadMore: Boolean) {
        val list = if (currentList.isEmpty()) {
            List(20) { ProductPlaceHolderViewItem() }
        } else {
            if (canLoadMore) productItems + LoadMoreViewItem() else productItems
        }
        super.submitList(list)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HomeViewItem>() {
            override fun areContentsTheSame(oldItem: HomeViewItem, newItem: HomeViewItem) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: HomeViewItem, newItem: HomeViewItem): Boolean {
                return if (oldItem is ProductViewItem && newItem is ProductViewItem) {
                    oldItem.product.productId == newItem.product.productId
                } else {
                    oldItem.viewType == newItem.viewType
                }
            }
        }
    }
}
