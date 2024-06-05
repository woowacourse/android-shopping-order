package woowacourse.shopping.presentation.ui.shopping

sealed interface FromShoppingToScreen {
    data class ProductDetail(
        val productId: Long,
        val quantity: Int,
    ) : FromShoppingToScreen

    data object Cart : FromShoppingToScreen
}
