package woowacourse.shopping.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import woowacourse.shopping.ui.cart.list.CartRoute
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationRoute
import woowacourse.shopping.ui.payment.PaymentRoute
import woowacourse.shopping.ui.productdetail.ProductDetailRoute
import woowacourse.shopping.ui.settings.SettingsRoute
import woowacourse.shopping.ui.shopping.ShoppingRoute
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(innerPadding: PaddingValues) {
    val navController = rememberNavController()

    NotificationPermissionRequester()

    NavHost(
        navController = navController,
        startDestination = ProductList,
    ) {
        composable<ProductList> {
            ShoppingRoute(
                modifier = Modifier.padding(innerPadding),
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
                onCartClick = { navController.navigate(CartGraph) },
                onSettingsClick = { navController.navigate(Settings) },
            )
        }

        composable<Settings> {
            SettingsRoute(
                modifier = Modifier.padding(innerPadding),
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<ProductDetail> {
            ProductDetailRoute(
                onCloseClick = { navController.popBackStack() },
                onLastViewedProductClick = { productId ->
                    navController.navigate(ProductDetail(productId)) {
                        popUpTo<ProductDetail> {
                            inclusive = true
                        }
                    }
                },
            )
        }

        navigation<CartGraph>(startDestination = Cart) {
            composable<Cart> { backStackEntry ->
                val cartFlowEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry<CartGraph>()
                    }

                CartRoute(
                    cartFlowEntry = cartFlowEntry,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { orderProducts ->
                        navController.navigate(CartRecommendation(orderProducts))
                    },
                )
            }

            composable<CartRecommendation>(
                typeMap = mapOf(typeOf<List<OrderProduct>>() to OrderProductListType),
            ) { backStackEntry ->
                val cartFlowEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry<CartGraph>()
                    }

                CartRecommendationRoute(
                    cartFlowEntry = cartFlowEntry,
                    modifier = Modifier.padding(innerPadding),
                    onProductClick = { productId ->
                        navController.navigate(ProductDetail(productId))
                    },
                    onOrderProductsReady = { orderProducts ->
                        navController.navigate(Payment(orderProducts = orderProducts)) {
                            popUpTo<CartRecommendation> {
                                inclusive = true
                            }
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable<Payment>(
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = "shopping://payment" },
                    ),
                typeMap = mapOf(typeOf<List<OrderProduct>>() to OrderProductListType),
            ) {
                PaymentRoute(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = { navController.popBackStack() },
                    onOrderCompleted = {
                        navController.navigate(ProductList) {
                            popUpTo<ProductList> {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }
    }
}
