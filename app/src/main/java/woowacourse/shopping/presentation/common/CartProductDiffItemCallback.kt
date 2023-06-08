package woowacourse.shopping.presentation.common

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.CartProductModel

class CartProductDiffItemCallback : DiffUtil.ItemCallback<CartProductModel>() {
    override fun areItemsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = (oldItem.productModel.id == newItem.productModel.id)

    override fun areContentsTheSame(
        oldItem: CartProductModel,
        newItem: CartProductModel,
    ): Boolean = oldItem == newItem
}
