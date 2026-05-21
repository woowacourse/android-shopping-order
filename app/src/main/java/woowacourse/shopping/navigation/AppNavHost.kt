package woowacourse.shopping.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.shopping.ShoppingScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Shopping
    ) {
        composable<Shopping> {
            ShoppingScreen(
                onCartClick = { navController.navigate(Cart) },
                onProductClick = { navController.navigate(ProductDetail(it)) },
            )
        }

        composable<ProductDetail> {
            ProductDetailScreen(
                onCloseClick = {
                    navController.navigate(Shopping) {
                        popUpTo<Shopping> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onAddToCartClick = {
                    navController.navigate(Shopping) {
                        popUpTo<Shopping> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLastViewedProductClick = {
                    navController.navigate(
                        ProductDetail(
                            id = it,
                            isFromBanner = true
                        )
                    )
                }
            )
        }

        composable<Cart> {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = {
                    navController.navigate(Shopping) {
                        popUpTo<Shopping> { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
