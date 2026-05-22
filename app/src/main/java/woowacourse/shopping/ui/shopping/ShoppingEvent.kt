package woowacourse.shopping.ui.shopping

sealed interface ShoppingEvent {
    data class ShowSnackBar(val message: String): ShoppingEvent
    data class NavigateToProductDetail(
        val selectedProductId: Long,
        val lastViewedProductId: Long? = null
    ): ShoppingEvent
    object NavigateToCart: ShoppingEvent
}