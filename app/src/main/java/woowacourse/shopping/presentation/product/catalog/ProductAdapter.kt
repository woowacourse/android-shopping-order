package woowacourse.shopping.presentation.product.catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandler
import woowacourse.shopping.presentation.product.catalog.viewHolder.LoadButtonViewHolder
import woowacourse.shopping.presentation.product.catalog.viewHolder.ProductViewHolder

class ProductAdapter(
    private var products: List<ProductUiModel>,
    private val catalogHandler: CatalogEventHandler,
    private val quantityHandler: ProductQuantityHandler,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var showLoadMoreButton = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == PRODUCT) {
            ProductViewHolder.from(parent, catalogHandler, quantityHandler)
        } else {
            LoadButtonViewHolder.from(parent, catalogHandler)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind(products[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (isLoadMoreButtonPosition(position)) {
            LOAD_BUTTON
        } else {
            PRODUCT
        }

    fun setData(newProducts: List<ProductUiModel>) {
        val oldList = products
        val updatedList = oldList + newProducts
        val oldSize = oldList.size

        products = updatedList

        newProducts.forEachIndexed { index, newItem ->
            val globalIndex = oldSize + index
            notifyItemInserted(globalIndex)
        }
    }


    override fun getItemCount(): Int = products.size + if (showLoadMoreButton) 1 else 0

    fun setLoadButtonVisible(visible: Boolean) {
        val previous = showLoadMoreButton
        showLoadMoreButton = visible
        if (previous != visible) {
            if (visible) {
                notifyItemInserted(products.size)
            } else {
                notifyItemRemoved(products.size)
            }
        }
    }

    fun updateProduct(updated: ProductUiModel) {
        val index = products.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            products =
                products.toMutableList().apply {
                    this[index] = updated
                }
            notifyItemChanged(index)
        }
    }

    fun isLoadMoreButtonPosition(position: Int): Boolean =
        showLoadMoreButton && position == products.size

    companion object {
        private const val PRODUCT = 1
        private const val LOAD_BUTTON = 2
    }
}
