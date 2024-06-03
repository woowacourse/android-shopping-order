package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.cart.CartUiModel

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
