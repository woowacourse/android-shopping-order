package woowacourse.shopping.ui.products.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.products.ProductUiModel

object ProductDiffUtil : DiffUtil.ItemCallback<ProductUiModel>() {
    override fun areItemsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
