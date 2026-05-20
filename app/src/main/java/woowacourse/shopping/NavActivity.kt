package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.navigation.ProductDetail
import woowacourse.shopping.navigation.ShoppingList
import woowacourse.shopping.presentation.productdetail.ProductDetailScreen
import woowacourse.shopping.presentation.productlist.ProductListScreen

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
                        onNavigateToCart = { },
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
            }
        }
    }
}
