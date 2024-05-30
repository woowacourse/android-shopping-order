package woowacourse.shopping.presentation.navigation

import woowacourse.shopping.presentation.cart.CartProductUi

interface ShoppingNavigator {
    fun navigateToProductDetail(productId: Long)

    fun navigateToCart()

    fun navigateToRecommend(orderIds: List<CartProductUi>)

    fun popBackStack(
        popUpTo: String,
        inclusive: Boolean = false,
    )

    fun popBackStack()
}
