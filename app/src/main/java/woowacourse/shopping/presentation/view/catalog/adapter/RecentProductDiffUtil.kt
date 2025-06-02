package woowacourse.shopping.presentation.view.catalog.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.ProductUiModel

object RecentProductDiffUtil : DiffUtil.ItemCallback<ProductUiModel>() {
    override fun areItemsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean = oldItem == newItem
}
