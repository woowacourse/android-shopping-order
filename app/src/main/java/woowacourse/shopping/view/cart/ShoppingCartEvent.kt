package woowacourse.shopping.view.cart

sealed interface ShoppingCartEvent {

    sealed interface UpdateProductEvent : ShoppingCartEvent {
        data class Success(val productId: Long, val count: Int) : UpdateProductEvent
        data class DELETE(val productId: Long) : UpdateProductEvent
    }

    sealed interface UpdateCheckItem : ShoppingCartEvent {
        data object Success : UpdateCheckItem
    }
}
