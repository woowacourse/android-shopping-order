package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import woowacourse.shopping.di.ShoppingRepositoryProvider
import woowacourse.shopping.ui.cart.CartRecommendationRouteScreen
import woowacourse.shopping.ui.cart.CartRouteScreen
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.order.OrderRouteScreen
import woowacourse.shopping.ui.order.OrderViewModel
import woowacourse.shopping.ui.productdetail.ProductDetailRouteScreen
import woowacourse.shopping.ui.setting.SettingRouteScreen
import woowacourse.shopping.ui.shopping.ShoppingRouteScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    pendingOrderNavigationToken: Long = 0L,
    onPendingOrderNavigationHandled: () -> Unit = {},
) {
    val pendingOrderEntryViewModel: PendingOrderEntryViewModel = viewModel()
    val pendingOrderSessionManager =
        remember {
            ShoppingRepositoryProvider.pendingOrderSessionManager
        }
    val pendingOrderEntryAction =
        pendingOrderEntryViewModel.pendingOrderEntryAction.collectAsStateWithLifecycle().value

    LaunchedEffect(pendingOrderNavigationToken) {
        pendingOrderEntryViewModel.handlePendingOrderEntryRequest(pendingOrderNavigationToken)
    }

    LaunchedEffect(pendingOrderEntryAction) {
        when (pendingOrderEntryAction) {
            PendingOrderEntryAction.OpenPendingOrder -> {
                navController.navigate(CartGraph) {
                    popUpTo(ShoppingRoute)
                    launchSingleTop = true
                }
                navController.navigate(OrderRoute(restorePendingOrder = true)) {
                    launchSingleTop = true
                }
            }

            PendingOrderEntryAction.Ignore -> Unit

            null -> return@LaunchedEffect
        }

        onPendingOrderNavigationHandled()
        pendingOrderEntryViewModel.consumePendingOrderEntryAction()
    }

    NavHost(
        navController = navController,
        startDestination = ShoppingRoute,
    ) {
        composable<ShoppingRoute> {
            ShoppingRouteScreen(
                onCartClick = { navController.navigate(CartGraph) },
                onSettingClick = { navController.navigate(SettingRoute) },
                onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                },
            )
        }

        composable<SettingRoute> {
            SettingRouteScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()

            ProductDetailRouteScreen(
                productId = route.productId,
                onCloseClick = { navController.popBackStack() },
                onLastViewedProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                },
            )
        }

        navigation<CartGraph>(startDestination = CartRoute) {
            composable<CartRoute> { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraph)
                    }
                val cartViewModel: CartViewModel = viewModel(parentEntry)
                val recommendationViewModel: CartRecommendationViewModel = viewModel(parentEntry)

                CartRouteScreen(
                    cartViewModel = cartViewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { selectedCartOrder ->
                        recommendationViewModel.startOrder(selectedCartOrder)
                        navController.navigate(CartRecommendationRoute)
                    },
                )
            }

            composable<OrderRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<OrderRoute>()
                val orderViewModel: OrderViewModel = viewModel()

                OrderRouteScreen(
                    orderViewModel = orderViewModel,
                    restorePendingOrder = route.restorePendingOrder,
                    restorePendingOrderSession = pendingOrderSessionManager::restore,
                    onBackClick = {
                        pendingOrderSessionManager.clear()
                        navController.popBackStack()
                    },
                    onPendingOrderUnavailable = {
                        navController.navigate(ShoppingRoute) {
                            popUpTo(CartGraph) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOrderCompleted = {
                        pendingOrderSessionManager.clear()
                        navController.navigate(ShoppingRoute) {
                            popUpTo(CartGraph) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable<CartRecommendationRoute> { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraph)
                    }
                val cartViewModel: CartViewModel = viewModel(parentEntry)
                val recommendationViewModel: CartRecommendationViewModel = viewModel(parentEntry)

                CartRecommendationRouteScreen(
                    cartViewModel = cartViewModel,
                    recommendationViewModel = recommendationViewModel,
                    onBackToCart = { navController.popBackStack() },
                    onProductClick = { productId ->
                        navController.navigate(ProductDetailRoute(productId))
                    },
                    onProceedToOrder = { selectedCartOrder ->
                        pendingOrderSessionManager.start(selectedCartOrder)
                        navController.navigate(OrderRoute(restorePendingOrder = true))
                    },
                )
            }
        }
    }
}
