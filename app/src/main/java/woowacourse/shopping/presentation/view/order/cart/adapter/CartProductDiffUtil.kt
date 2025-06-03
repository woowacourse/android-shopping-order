package woowacourse.shopping.presentation.view.order.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.CartProductUiModel

object CartProductDiffUtil : DiffUtil.ItemCallback<CartProductUiModel>() {
    override fun areItemsTheSame(
        oldItem: CartProductUiModel,
        newItem: CartProductUiModel,
    ): Boolean = oldItem.productId == newItem.productId

    override fun areContentsTheSame(
        oldItem: CartProductUiModel,
        newItem: CartProductUiModel,
    ): Boolean = oldItem == newItem
}
