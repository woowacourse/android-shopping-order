package woowacourse.shopping.view.cart.adapter

import woowacourse.shopping.domain.model.CartItem

sealed class ShoppingCartItem {
    data class CartProductItem(val cartItem: CartItem) : ShoppingCartItem()

    data object SkeletonItem : ShoppingCartItem()
}
