package woowacourse.shopping.presentation.navigation

import woowacourse.shopping.presentation.cart.CartProductUi

interface ShoppingNavigator {
    fun navigateToProductDetail(
        productId: Long,
        addBackStack: Boolean,
        tag: String?,
    )

    fun navigateToCart(
        addBackStack: Boolean,
        tag: String?,
    )

    fun navigateToRecommend(
        orderIds: List<CartProductUi>,
        addBackStack: Boolean,
        tag: String?,
    )

    fun navigateToPayment(
        orderIds: List<CartProductUi>,
        addBackStack: Boolean,
        tag: String?,
    )

    fun popBackStack(
        popUpTo: String,
        inclusive: Boolean = false,
    )

    fun popBackStack()
}
