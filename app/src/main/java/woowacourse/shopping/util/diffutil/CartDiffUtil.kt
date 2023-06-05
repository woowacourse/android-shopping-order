package woowacourse.shopping.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.model.CartProductModel

object CartDiffUtil : DiffUtil.ItemCallback<CartProductModel>() {
    override fun areItemsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = oldItem == newItem
}
