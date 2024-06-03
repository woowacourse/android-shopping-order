package woowacourse.shopping.ui.cart.item

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.cart.item.CartUiModel

object CartDiffUtil : DiffUtil.ItemCallback<CartUiModel>() {
    override fun areItemsTheSame(
        oldItem: CartUiModel,
        newItem: CartUiModel,
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: CartUiModel,
        newItem: CartUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
