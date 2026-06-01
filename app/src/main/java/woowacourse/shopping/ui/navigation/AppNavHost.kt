package woowacourse.shopping.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.UiEvent
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartUiEvent
import woowacourse.shopping.ui.cart.CartUiState
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.common.SettingsScreen
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.payment.PaymentUiEvent
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.ui.productDetail.ProductDetailScreen
import woowacourse.shopping.ui.productDetail.ProductDetailUiEvent
import woowacourse.shopping.ui.productDetail.ProductDetailViewModel
import woowacourse.shopping.ui.productList.ProductListScreen
import woowacourse.shopping.ui.productList.ProductListUiEvent
import woowacourse.shopping.ui.productList.ProductListViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appContainer: AppContainer,
) {
    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
        modifier = modifier,
    ) {
        composable<ProductListRoute> {
            val viewModel: ProductListViewModel =
                viewModel(
                    factory =
                        ProductListViewModel.factory(
                            productRepository = appContainer.productRepository,
                            cartRepository = appContainer.cartRepository,
                            recentProductRepository = appContainer.recentProductRepository,
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(viewModel) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is ProductListUiEvent.ShowSnackbar ->
                            snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                ProductListScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    onCartClick = {
                        navController.navigate(CartRoute)
                    },
                    onSettingsClick = {
                        navController.navigate(SettingsRoute)
                    },
                    onProductClick = { product ->
                        navController.navigate(ProductDetailRoute(productId = product.id))
                    },
                )
            }
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()
            val viewModel: ProductDetailViewModel =
                viewModel(
                    factory =
                        ProductDetailViewModel.factory(
                            productId = route.productId,
                            openedFromLastViewed = false,
                            productRepository = appContainer.productRepository,
                            cartRepository = appContainer.cartRepository,
                            recentProductRepository = appContainer.recentProductRepository,
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(viewModel) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is ProductDetailUiEvent.ShowSnackbar ->
                            snackbarHostState.showSnackbar(event.message)
                        ProductDetailUiEvent.AddedToCart ->
                            navController.navigate(CartRoute)
                    }
                }
            }

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                ProductDetailScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    onAddToCartClick = {
                        viewModel.addToCart()
                    },
                    onLastViewedProductClick = { product ->
                        navController.navigate(ProductDetailRoute(productId = product.id))
                    },
                )
            }
        }

        composable<CartRoute> {
            val viewModel: CartViewModel =
                viewModel(
                    factory =
                        CartViewModel.factory(
                            cartRepository = appContainer.cartRepository,
                            recentProductRepository = appContainer.recentProductRepository,
                            productRepository = appContainer.productRepository,
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(viewModel) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is CartUiEvent.ShowSnackbar ->
                            snackbarHostState.showSnackbar(event.message)
                        is CartUiEvent.OrderRequested ->
                            navController.navigate(
                                PaymentRoute(selectedItemIds = event.selectedItemIds),
                            )
                    }
                }
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                CartScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    onClickClose = {
                        navController.popBackStack()
                    },
                )
            }
        }

        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable<PaymentRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PaymentRoute>()
            val viewModel: PaymentViewModel =
                viewModel(
                    factory =
                        PaymentViewModel.factory(
                            cartRepository = appContainer.cartRepository,
                            couponRepository = appContainer.couponRepository,
                            selectedItemIds = route.selectedItemIds.toSet(),
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(viewModel) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is PaymentUiEvent.ShowMessage ->
                            snackbarHostState.showSnackbar(event.message)
                        PaymentUiEvent.OrderSucceeded ->
                            navController.navigate(ProductListRoute) {
                                popUpTo<PaymentRoute> { inclusive = true }
                                launchSingleTop = true
                            }
                    }
                }
            }

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                PaymentScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                    selectedItemIds = route.selectedItemIds,
                    onClose = { navController.popBackStack() },
                    onPayClick = viewModel::onClickPay,
                )
            }
        }
    }
}
