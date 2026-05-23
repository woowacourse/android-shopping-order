package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.presentation.cart.CartItemListScreen
import woowacourse.shopping.presentation.order.OrderScreen
import woowacourse.shopping.presentation.productdetail.ProductDetailScreen
import woowacourse.shopping.presentation.productlist.ProductListScreen
import woowacourse.shopping.presentation.recommend.RecommendItemScreen

class NavActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = ShoppingList,
            ) {
                composable<ShoppingList> {
                    ProductListScreen(
                        onNavigateToCart = { navController.navigate(route = CartItemList) },
                        onProductClick = { productId ->
                            navController.navigate(route = ProductDetail(productId))
                        },
                    )
                }

                composable<ProductDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<ProductDetail>()
                    ProductDetailScreen(
                        productId = route.productId,
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

                composable<RecommendItem> { backStackEntry ->
                    val route = backStackEntry.toRoute<RecommendItem>()
                    RecommendItemScreen(
                        productIds = route.productIds,
                        onBackClick = { navController.popBackStack() },
                        onOrderClick = { productIds ->
                            navController.navigate(route = OrderItem(productIds))
                        },
                    )
                }

                composable<OrderItem> { backStackEntry ->
                    val route = backStackEntry.toRoute<OrderItem>()
                    OrderScreen(
                        productIds = route.productIds,
                        onBackClick = { navController.popBackStack() },
                        onOrderSuccess = {
                            navController.navigate(ShoppingList) {
                                popUpTo(ShoppingList) { inclusive = false }
                            }
                        },
                    )
                }
            }
        }
    }
}
