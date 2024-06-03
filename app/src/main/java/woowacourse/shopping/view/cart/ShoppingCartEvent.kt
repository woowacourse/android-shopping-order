package woowacourse.shopping.view.cart

import woowacourse.shopping.view.cart.model.ShoppingCart

sealed interface ShoppingCartEvent {
    sealed interface UpdateProductEvent : ShoppingCartEvent {
        data class Success(val productId: Long, val count: Int) : UpdateProductEvent

        data class DELETE(val productId: Long) : UpdateProductEvent
    }

    sealed interface SendCartItem : ShoppingCartEvent {
        data class Success(val shoppingCart: ShoppingCart) : SendCartItem
    }
}
