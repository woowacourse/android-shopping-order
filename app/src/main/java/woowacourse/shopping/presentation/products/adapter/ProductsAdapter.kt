package woowacourse.shopping.presentation.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemRecentProductsBinding
import woowacourse.shopping.presentation.products.ProductsActionHandler
import woowacourse.shopping.presentation.products.ProductsUiState
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel
import woowacourse.shopping.presentation.products.uimodel.RecentProductUiModel

class ProductsAdapter(
    private var productsUiState: ProductsUiState =
        ProductsUiState(),
    private var recentProductUiModels: List<RecentProductUiModel> = listOf(),
    private val actionHandler: ProductsActionHandler,
) : RecyclerView.Adapter<ProductsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (ProductsViewType.entries[viewType]) {
            ProductsViewType.RecentProducts -> {
                val binding = ItemRecentProductsBinding.inflate(inflater, parent, false)
                ProductsViewHolder.RecentProductsViewHolder(binding, actionHandler)
            }

            ProductsViewType.Product -> {
                val binding = ItemProductBinding.inflate(inflater, parent, false)
                ProductsViewHolder.ProductViewHolder(binding, actionHandler)
            }

            ProductsViewType.LoadMore -> {
                val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
                ProductsViewHolder.LoadMoreViewHolder(binding, actionHandler)
            }
        }
    }

    override fun onBindViewHolder(
        holder: ProductsViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductsViewHolder.RecentProductsViewHolder -> {
                holder.bind(recentProductUiModels)
            }

            is ProductsViewHolder.ProductViewHolder -> {
                holder.bind(
                    productsUiState.productUiModels[position - 1],
                )
            }

            is ProductsViewHolder.LoadMoreViewHolder -> {
                holder.bind(
                    productsUiState.isLast,
                )
            }
        }
    }

    override fun getItemCount(): Int = productsUiState.productUiModels.size + ProductsViewType.entries.size - 1

    override fun getItemViewType(position: Int): Int {
        return when {
            (position == 0) -> ProductsViewType.RecentProducts.ordinal
            (position == itemCount - 1) -> ProductsViewType.LoadMore.ordinal
            else -> ProductsViewType.Product.ordinal
        }
    }

    fun updateProducts(updatedUiState: ProductsUiState) {
        val products = productsUiState.productUiModels
        val updatedProducts = updatedUiState.productUiModels

        productsUiState = updatedUiState
        val diff = updatedProducts - products.toSet()
        notifyItemRangeInserted(products.size, diff.size + 1)

        diff.forEach { changeProduct(it) }
        // notifyDataSetChanged()
    }

    private fun changeProduct(newProduct: ProductUiModel) {
        val position =
            productsUiState.productUiModels.indexOfFirst { it.product.id == newProduct.product.id }
        notifyItemChanged(position + 1)
    }

    private fun isExistedRecentProducts(): Boolean = recentProductUiModels.isEmpty()

    fun findProductsLastPosition(lastPosition: Int): Int {
        if (isExistedRecentProducts()) return lastPosition - 1
        return lastPosition
    }
}
