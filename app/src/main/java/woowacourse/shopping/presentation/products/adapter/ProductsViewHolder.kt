package woowacourse.shopping.presentation.products.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemRecentProductsBinding
import woowacourse.shopping.presentation.products.ProductsActionHandler
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel
import woowacourse.shopping.presentation.products.uimodel.RecentProductUiModel

sealed class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val actionHandler: ProductsActionHandler,
    ) :
        ProductsViewHolder(binding.root) {
        fun bind(productUiModel: ProductUiModel) {
            binding.productUiModel = productUiModel
            binding.actionHandler = actionHandler
        }
    }

    class RecentProductsViewHolder(
        binding: ItemRecentProductsBinding,
        private val actionHandler: ProductsActionHandler,
    ) : ProductsViewHolder(binding.root) {
        private val adapter by lazy {
            RecentProductsAdapter(actionHandler)
        }

        init {
            binding.rvRecentProduct.itemAnimator = null
            binding.rvRecentProduct.adapter = adapter
        }

        fun bind(recentProducts: List<RecentProductUiModel>) {
            adapter.submitList(recentProducts)
        }
    }

    class LoadMoreViewHolder(
        private val binding: ItemLoadMoreBinding,
        private val actionHandler: ProductsActionHandler,
    ) : ProductsViewHolder(binding.root) {
        fun bind(isLastPage: Boolean) {
            binding.isLastPage = isLastPage
            binding.actionHandler = actionHandler
        }
    }
}
