package woowacourse.shopping.ui.state

import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProducts

data class ShoppingUiState(
    val products: Products = Products(),
    val recentlyViewedProducts: Products = Products(),
    val cart: PurchaseProducts = PurchaseProducts(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
) {
    fun totalCartCount() = cart.totalCount()
}
