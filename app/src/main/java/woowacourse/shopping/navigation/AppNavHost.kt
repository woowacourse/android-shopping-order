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
                viewModel = TODO(),
                onCartClick = TODO(),
                onProductClick = TODO(),
                onRecentProductClick = TODO()
            )
        }
        
        composable<ProductDetail> {
            ProductDetailScreen(
                viewModel = TODO(),
                onCloseClick = TODO(),
                onAddToCartClick = TODO(),
                onLastViewedProductClick = TODO()
            )
        }
        
        composable<Cart> {
            CartScreen(
                viewModel = TODO(),
                onBackClick = TODO(),
                onOrderClick = TODO()
            )
        }
    }
}