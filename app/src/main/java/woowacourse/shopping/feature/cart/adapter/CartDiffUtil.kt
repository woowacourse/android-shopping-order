package woowacourse.shopping.feature.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.domain.model.Cart

class CartDiffUtil : DiffUtil.ItemCallback<Cart>() {
    override fun areItemsTheSame(
        oldItem: Cart,
        newItem: Cart,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Cart,
        newItem: Cart,
    ): Boolean = oldItem == newItem
}
