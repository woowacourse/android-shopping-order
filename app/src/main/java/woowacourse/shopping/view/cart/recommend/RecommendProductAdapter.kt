package woowacourse.shopping.view.cart.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.databinding.ItemProductPlaceholderBinding
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.home.product.HomeViewItem
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.PRODUCT_PLACEHOLDER_VIEW_TYPE
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.PRODUCT_VIEW_TYPE
import woowacourse.shopping.view.home.product.LoadMoreButtonViewHolder
import woowacourse.shopping.view.home.product.ProductDiffUtil
import woowacourse.shopping.view.home.product.ProductPlaceholderViewHolder

class RecommendProductAdapter(
    private val productClickListener: RecommendProductEventListener,
    private val quantityClickListener: QuantityEventListener,
) : ListAdapter<HomeViewItem, RecyclerView.ViewHolder>(ProductDiffUtil) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PRODUCT_VIEW_TYPE -> {
                RecommendedProductViewHolder(
                    ItemRecommendProductBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
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
        val shoppingItem = currentList[position]
        if (holder is RecommendedProductViewHolder) {
            holder.bind(
                (shoppingItem as HomeViewItem.ProductViewItem),
                productClickListener,
                quantityClickListener
            )
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun loadData(
        productItems: List<HomeViewItem.ProductViewItem>,
    ) {
        if (isFirstLoad()) {
            submitList(null)
        }
        submitList(productItems)
    }

    fun updateData(updatedItems: List<HomeViewItem.ProductViewItem>) {
        updatedItems.forEach { updatedItem ->
            updateProductQuantity(updatedItem)
        }
    }

    fun updateProductQuantity(updatedProductItem: HomeViewItem.ProductViewItem) {
        if (!isFirstLoad()) {
            val position =
                currentList.indexOfFirst { item ->
                    item is HomeViewItem.ProductViewItem && item.product.id == updatedProductItem.product.id
                }

            if (position != -1) {
                currentList[position] = updatedProductItem
                notifyItemChanged(position)
            }
        }
    }

    private fun isFirstLoad() = currentList.all { it.viewType == PRODUCT_PLACEHOLDER_VIEW_TYPE }
}
