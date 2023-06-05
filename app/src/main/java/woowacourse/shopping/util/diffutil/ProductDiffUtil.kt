package woowacourse.shopping.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.model.CartProductModel

object ProductDiffUtil : DiffUtil.ItemCallback<CartProductModel>() {
    override fun areItemsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = oldItem.product.id == newItem.product.id

    override fun areContentsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = oldItem == newItem
}
