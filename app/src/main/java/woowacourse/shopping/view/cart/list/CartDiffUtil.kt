package woowacourse.shopping.view.cart.list

import androidx.recyclerview.widget.DiffUtil

object CartDiffUtil : DiffUtil.ItemCallback<ShoppingCartViewItem>() {
    override fun areItemsTheSame(
        oldItem: ShoppingCartViewItem,
        newItem: ShoppingCartViewItem,
    ): Boolean {
        if (oldItem is ShoppingCartViewItem.CartViewItem && newItem is ShoppingCartViewItem.CartViewItem) {
            return oldItem.cartItem.cartItemId == newItem.cartItem.cartItemId
        }
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(
        oldItem: ShoppingCartViewItem,
        newItem: ShoppingCartViewItem,
    ): Boolean {
        return oldItem == newItem
    }
}
