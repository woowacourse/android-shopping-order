package woowacourse.shopping.ui.cart.recommend

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel

object RecommendProductDiffUtil : DiffUtil.ItemCallback<ProductWithQuantityUiModel>() {
    override fun areItemsTheSame(
        oldItem: ProductWithQuantityUiModel,
        newItem: ProductWithQuantityUiModel,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ProductWithQuantityUiModel,
        newItem: ProductWithQuantityUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
