package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Product

object CartRecommendDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem.productDetail.id == newItem.productDetail.id

    override fun areContentsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem == newItem
}
