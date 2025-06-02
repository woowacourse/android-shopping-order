package woowacourse.shopping.feature.cart.adapter

import woowacourse.shopping.domain.model.CartItem

sealed class CartListItem {
    data class CartData(
        val cartItem: CartItem,
    ) : CartListItem()

    data object Skeleton : CartListItem()
}
