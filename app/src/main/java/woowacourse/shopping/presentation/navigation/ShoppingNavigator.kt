package woowacourse.shopping.presentation.navigation

interface ShoppingNavigator {
    fun navigateToProductDetail(productId: Long)

    fun navigateToCart()

    fun navigateToRecommend(orderIds: List<Long>)

    fun popBackStack(
        popUpTo: String,
        inclusive: Boolean = false,
    )

    fun popBackStack()
}
