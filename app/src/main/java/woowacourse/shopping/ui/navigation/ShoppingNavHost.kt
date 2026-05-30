package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.recommend.RecommendScreen
import woowacourse.shopping.ui.cart.recommend.RecommendViewModel
import woowacourse.shopping.ui.order.OrderScreen
import woowacourse.shopping.ui.order.OrderViewModel
import woowacourse.shopping.ui.productDetail.ProductDetailScreen
import woowacourse.shopping.ui.productDetail.ProductDetailViewModel
import woowacourse.shopping.ui.productList.ProductListScreen
import woowacourse.shopping.ui.productList.ProductListViewModel
import woowacourse.shopping.ui.setting.SettingScreen
import woowacourse.shopping.ui.setting.SettingViewModel

@Composable
fun ShoppingNavHost(
    startCartIds: List<Int>? = null,
    onCartIdsConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()

    LaunchedEffect(startCartIds) {
        if (startCartIds != null) {
            navController.navigate(OrderRoute.Order(startCartIds))
            onCartIdsConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = ProductRoute.ProductList,
    ) {
        composable<ProductRoute.ProductList> {
            val viewModel: ProductListViewModel = viewModel(factory = ProductListViewModel.Factory)
            ProductListScreen(
                viewModel = viewModel,
                onCartClick = { navController.navigate(CartGraph) },
                onSettingClick = { navController.navigate(SettingRoute.Setting) },
                onProductClick = { productId ->
                    navController.navigate(
                        ProductRoute.Detail(
                            productId,
                            openedFromLastViewed = false,
                        ),
                    )
                },
            )
        }

        composable<ProductRoute.Detail> {
            val viewModel: ProductDetailViewModel =
                viewModel(factory = ProductDetailViewModel.Factory)
            ProductDetailScreen(
                viewModel = viewModel,
                onCloseClick = { navController.popBackStack() },
                onAddToCartClick = { viewModel.addToCart() },
                onLastViewedProductClick = { product ->
                    navController.navigate(
                        ProductRoute.Detail(
                            product.id,
                            openedFromLastViewed = true,
                        ),
                    )
                },
            )
        }

        navigation<CartGraph>(startDestination = CartRoute.Cart) {
            composable<CartRoute.Cart> { entry ->
                val cartGraphEntry =
                    remember(entry) {
                        navController.getBackStackEntry<CartGraph>()
                    }
                val cartViewModel: CartViewModel =
                    viewModel(
                        viewModelStoreOwner = cartGraphEntry,
                        factory = CartViewModel.Factory,
                    )
                CartScreen(
                    viewModel = cartViewModel,
                    onClickClose = { navController.popBackStack() },
                    onNavigateToRecommend = { navController.navigate(CartRoute.Recommend) },
                )
            }

            composable<CartRoute.Recommend> { entry ->
                val cartGraphEntry =
                    remember(entry) {
                        navController.getBackStackEntry<CartGraph>()
                    }
                val cartViewModel: CartViewModel =
                    viewModel(
                        viewModelStoreOwner = cartGraphEntry,
                        factory = CartViewModel.Factory,
                    )
                val recommendViewModel: RecommendViewModel =
                    viewModel(factory = RecommendViewModel.Factory)
                RecommendScreen(
                    cartViewModel = cartViewModel,
                    recommendViewModel = recommendViewModel,
                    onClickClose = { navController.popBackStack() },
                    onNavigateToOrder = { cartIds -> navController.navigate(OrderRoute.Order(cartIds)) },
                )
            }
        }

        composable<OrderRoute.Order> {
            val viewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
            OrderScreen(
                orderViewModel = viewModel,
                onClickClose = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate(ProductRoute.ProductList) {
                        popUpTo<ProductRoute.ProductList> { inclusive = true }
                    }
                },
            )
        }

        composable<SettingRoute.Setting> {
            val viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory)
            SettingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
    LaunchedEffect(startCartIds) {
        if (startCartIds != null) {
            navController.navigate(OrderRoute.Order(startCartIds))
        }
    }
}
