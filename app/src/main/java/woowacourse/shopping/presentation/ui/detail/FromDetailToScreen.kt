package woowacourse.shopping.presentation.ui.detail

sealed interface FromDetailToScreen {
    data class ProductDetail(val productId: Long) : FromDetailToScreen

    data class ShoppingWithUpdated(
        val productId: Long,
        val quantity: Int,
    ) : FromDetailToScreen

    data object Shopping : FromDetailToScreen
}
