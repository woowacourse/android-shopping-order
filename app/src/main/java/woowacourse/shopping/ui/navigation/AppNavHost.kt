package woowacourse.shopping.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.productDetail.ProductDetailScreen
import woowacourse.shopping.ui.productDetail.ProductDetailViewModel
import woowacourse.shopping.ui.productList.ProductListScreen
import woowacourse.shopping.ui.productList.ProductListViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appContainer: AppContainer,
) {
    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
        modifier = modifier,
    ) {
        composable<ProductListRoute> {
            val productListViewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModel.factory(
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentProductRepository = appContainer.recentProductRepository,
                ),
            )
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProductListScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = productListViewModel,
                    onCartClick = {
                        navController.navigate(CartRoute)
                    },
                    onProductClick = { product ->
                        navController.navigate(ProductDetailRoute(productId = product.id))
                    },
                )
            }
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()
            val productDetailViewModel: ProductDetailViewModel = viewModel(
                factory = ProductDetailViewModel.factory(
                    productId = route.productId,
                    openedFromLastViewed = false,
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentProductRepository = appContainer.recentProductRepository,
                ),
            )
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProductDetailScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = productDetailViewModel,
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    onAddToCartClick = {
                        navController.navigate(CartRoute)
                    },
                    onLastViewedProductClick = { product ->
                        navController.navigate(ProductDetailRoute(productId = product.id))
                    },
                )
            }
        }

        composable<CartRoute> {
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModel.factory(
                    cartRepository = appContainer.cartRepository,
                    recentProductRepository = appContainer.recentProductRepository,
                    productRepository = appContainer.productRepository,
                ),
            )
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CartScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = cartViewModel,
                    onClickClose = {
                        navController.popBackStack()
                    },
                    onOrderComplete = {
                        navController.navigate(ProductListRoute) {
                            popUpTo<CartRoute> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}


