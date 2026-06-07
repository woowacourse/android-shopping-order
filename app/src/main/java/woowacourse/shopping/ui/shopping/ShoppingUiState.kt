package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProducts

data class ShoppingUiState(
    val products: Products = Products(),
    val recentlyViewedProducts: Products = Products(),
    val cart: PurchaseProducts = PurchaseProducts(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val notificationAllowed: Boolean = false,
) {
    fun totalCartCount() = cart.totalCount()
}
