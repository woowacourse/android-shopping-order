package woowacourse.shopping.view.cart.adapter

import woowacourse.shopping.domain.model.cart.CartItem

sealed class ShoppingCartItem {
    data class CartProductItem(val cartItem: CartItem) : ShoppingCartItem()

    data object SkeletonItem : ShoppingCartItem()
}
