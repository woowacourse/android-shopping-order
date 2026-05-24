package woowacourse.shopping.ui.cart.component

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money

data class CartSelection(
    val ids: ImmutableList<String>,
) {
    fun filterSameIds(cartItems: List<CartItem>): CartSelection {
        val itemIds = cartItems.map { it.id }.toSet()

        return copy(ids = ids.filter { it in itemIds }.toImmutableList())
    }

    fun contains(cartItemId: String): Boolean = cartItemId in ids

    fun isAllSelected(cartItems: List<CartItem>): Boolean =
        cartItems.isNotEmpty() &&
            cartItems.all { it.id in ids }

    fun totalPrice(cartItems: List<CartItem>): Money =
        cartItems
            .filter { it.id in ids }
            .fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }

    fun selectedCount(cartItems: List<CartItem>): Int = cartItems.filter { it.id in ids }.sumOf { it.quantity }
}
