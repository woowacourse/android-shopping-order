package woowacourse.shopping.ui.cart.cartitem

import androidx.recyclerview.widget.DiffUtil

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
