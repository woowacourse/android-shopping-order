package woowacourse.shopping.presentation.util

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class ProductUiModelDiffCallback : DiffUtil.ItemCallback<ProductUiModel>() {
    override fun areItemsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean = oldItem == newItem
}
