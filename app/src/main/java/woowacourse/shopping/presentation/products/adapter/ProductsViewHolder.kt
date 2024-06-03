package woowacourse.shopping.presentation.products.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemRecentProductsBinding
import woowacourse.shopping.presentation.products.adapter.recent.OnClickRecentProductItem
import woowacourse.shopping.presentation.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.presentation.products.adapter.recent.RecentProductsAdapter
import woowacourse.shopping.presentation.products.adapter.type.ProductUiModel
import woowacourse.shopping.presentation.utils.AddCartQuantityBundle
import woowacourse.shopping.presentation.utils.OnDecreaseProductQuantity
import woowacourse.shopping.presentation.utils.OnIncreaseProductQuantity

sealed class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    class ProductViewHolder(private val binding: ItemProductBinding) :
        ProductsViewHolder(binding.root) {
        fun bind(
            productUiModel: ProductUiModel,
            onClickProductItem: OnClickProductItem,
            onIncreaseProductQuantity: OnIncreaseProductQuantity,
            onDecreaseProductQuantity: OnDecreaseProductQuantity,
        ) {
            binding.productUiModel = productUiModel
            binding.addCartQuantityBundle =
                AddCartQuantityBundle(
                    productUiModel.productId,
                    productUiModel.quantity,
                    onIncreaseProductQuantity,
                    onDecreaseProductQuantity,
                )
            binding.ivProduct.setOnClickListener {
                onClickProductItem(productUiModel.productId)
            }
        }
    }

    class RecentProductsViewHolder(
        binding: ItemRecentProductsBinding,
        onClickRecentProductItem: OnClickRecentProductItem,
    ) : ProductsViewHolder(binding.root) {
        private val adapter by lazy {
            RecentProductsAdapter(onClickRecentProductItem = onClickRecentProductItem)
        }

        init {
            binding.rvRecentProduct.itemAnimator = null
            binding.rvRecentProduct.adapter = adapter
        }

        fun bind(recentProducts: List<RecentProductUiModel>) {
            adapter.submitList(recentProducts)
        }
    }
}

typealias OnClickProductItem = (productId: Int) -> Unit
