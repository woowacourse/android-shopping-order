package woowacourse.shopping.domain.model.cart

sealed interface UpdateCartItemResult {
    data class UPDATED(val cartItemResult: CartItemResult) : UpdateCartItemResult

    data class DELETE(val cartItemId: Long) : UpdateCartItemResult

    data object ADD : UpdateCartItemResult
}
