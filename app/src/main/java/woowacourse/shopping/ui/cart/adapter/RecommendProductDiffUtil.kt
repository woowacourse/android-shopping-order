package woowacourse.shopping.ui.cart.adapter

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
