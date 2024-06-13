package woowacourse.shopping.presentation.ui.shopping

sealed interface FromShoppingToScreen {
    data class ProductDetail(
        val productId: Long,
        val cartId: Long,
    ) : FromShoppingToScreen

    data object Cart : FromShoppingToScreen
}
