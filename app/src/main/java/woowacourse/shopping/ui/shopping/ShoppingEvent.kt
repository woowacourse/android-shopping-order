package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.PurchaseProduct

sealed interface ShoppingEvent {
    data class ShowSnackBar(val message: String): ShoppingEvent
    data class NavigateToProductDetail(
        val selectedProductId: Long,
        val lastViewedProductId: Long? = null
    ): ShoppingEvent
    object NavigateToCart: ShoppingEvent
    data class AddToCart(val purchaseProduct: PurchaseProduct): ShoppingEvent
    data class UpdateCount(val productID: Long, val updateAmount: Int): ShoppingEvent

    data class RemoveFormCart(val purchaseProductId: Long): ShoppingEvent
    object LoadMore: ShoppingEvent
}