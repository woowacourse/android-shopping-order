package woowacourse.shopping.feature.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.feature.cart.adapter.CartAdapter.Companion.QUANTITY_CHANGED_PAYLOAD

class CartDiffCallback : DiffUtil.ItemCallback<CartListItem>() {
    override fun areItemsTheSame(
        oldItem: CartListItem,
        newItem: CartListItem,
    ): Boolean =
        when {
            oldItem is CartListItem.Skeleton && newItem is CartListItem.Skeleton -> true
            oldItem is CartListItem.CartData && newItem is CartListItem.CartData ->
                oldItem.cartItem.goods.id == newItem.cartItem.goods.id
            else -> false
        }

    override fun areContentsTheSame(
        oldItem: CartListItem,
        newItem: CartListItem,
    ): Boolean = oldItem == newItem

    override fun getChangePayload(
        oldItem: CartListItem,
        newItem: CartListItem,
    ): Any? {
        if (oldItem is CartListItem.CartData && newItem is CartListItem.CartData) {
            val oldCart = oldItem.cartItem
            val newCart = newItem.cartItem
            if (oldCart.goods == newCart.goods && oldCart.quantity != newCart.quantity) {
                return QUANTITY_CHANGED_PAYLOAD
            }
        }
        return null
    }
}
