package woowacourse.shopping.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.presentation.cart.CartItemListScreen
import woowacourse.shopping.presentation.order.OrderScreen
import woowacourse.shopping.presentation.productdetail.ProductDetailScreen
import woowacourse.shopping.presentation.productlist.ProductListScreen
import woowacourse.shopping.presentation.recommend.RecommendItemScreen
import woowacourse.shopping.presentation.settings.NotificationSettingScreen

@Composable
fun ShoppingNavGraph(
    navController: NavHostController,
    onEnterOrder: (List<Long>) -> Unit,
    onOrderSuccess: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingList,
    ) {
        composable<ShoppingList> {
            ProductListScreen(
                onNavigateToCart = { navController.navigate(route = CartItemList) },
                onNavigateToNotificationSetting = { navController.navigate(route = NotificationSetting) },
                onProductClick = { productId ->
                    navController.navigate(route = ProductDetail(productId))
                },
            )
        }

        composable<NotificationSetting> {
            NotificationSettingScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<ProductDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetail>()
            ProductDetailScreen(
                onRecentProductClick = { lastProductId ->
                    navController.navigate(route = ProductDetail(lastProductId)) {
                        popUpTo(route = ProductDetail(route.productId)) {
                            inclusive = true
                        }
                    }
                },
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<CartItemList> {
            CartItemListScreen(
                onBack = { navController.popBackStack() },
                onOrderClick = { productIds ->
                    navController.navigate(route = RecommendItem(productIds))
                },
            )
        }

        composable<RecommendItem> {
            RecommendItemScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { productIds ->
                    navController.navigate(route = OrderItem(productIds))
                    onEnterOrder(productIds)
                },
            )
        }

        composable<OrderItem> {
            OrderScreen(
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = {
                    onOrderSuccess()
                    navController.navigate(ShoppingList) {
                        popUpTo(ShoppingList) { inclusive = false }
                    }
                },
            )
        }
    }
}
