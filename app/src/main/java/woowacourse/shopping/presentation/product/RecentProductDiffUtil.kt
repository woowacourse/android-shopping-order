package woowacourse.shopping.presentation.product

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Product

object RecentProductDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem.productId == newItem.productId

    override fun areContentsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem == newItem
}
