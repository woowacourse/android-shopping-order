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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.UiEvent
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartUiState
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.payment.PaymentViewModel
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
            val productListViewModel: ProductListViewModel =
                viewModel(
                    factory =
                        ProductListViewModel.factory(
                            productRepository = appContainer.productRepository,
                            cartRepository = appContainer.cartRepository,
                            recentProductRepository = appContainer.recentProductRepository,
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(productListViewModel) {
                productListViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                        UiEvent.NavigateToCart -> Unit
                        UiEvent.NavigateToProductList -> Unit
                        UiEvent.NavigateToPayment -> navController.navigate(PaymentRoute)
                    }
                }
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
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
            val productDetailViewModel: ProductDetailViewModel =
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

            LaunchedEffect(productDetailViewModel) {
                productDetailViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                        UiEvent.NavigateToCart -> navController.navigate(CartRoute)
                        UiEvent.NavigateToProductList -> Unit
                        UiEvent.NavigateToPayment -> Unit
                    }
                }
            }

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                ProductDetailScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = productDetailViewModel,
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    onAddToCartClick = {
                        productDetailViewModel.addToCart()
                    },
                    onLastViewedProductClick = { product ->
                        navController.navigate(ProductDetailRoute(productId = product.id))
                    },
                )
            }
        }

        composable<CartRoute> {
            val cartViewModel: CartViewModel =
                viewModel(
                    factory =
                        CartViewModel.factory(
                            cartRepository = appContainer.cartRepository,
                            recentProductRepository = appContainer.recentProductRepository,
                            productRepository = appContainer.productRepository,
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(cartViewModel) {
                cartViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                        UiEvent.NavigateToCart -> Unit
                        UiEvent.NavigateToPayment -> {
                            val selectedIds = (cartViewModel.uiState.value as? CartUiState.Success)?.selectedItems?.toList() ?: emptyList()
                            navController.navigate(PaymentRoute(selectedItemIds = selectedIds))
                        }

                        UiEvent.NavigateToProductList ->
                            navController.navigate(ProductListRoute) {
                                popUpTo<CartRoute> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                    }
                }
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                CartScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = cartViewModel,
                    onClickClose = {
                        navController.popBackStack()
                    },
                )
            }
        }

        composable<PaymentRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PaymentRoute>()
            val paymentViewModel: PaymentViewModel =
                viewModel(
                    factory =
                        PaymentViewModel.factory(
                            cartRepository = appContainer.cartRepository,
                            couponRepository = appContainer.couponRepository,
                            selectedItemIds = route.selectedItemIds.toSet(),
                        ),
                )
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(paymentViewModel) {
                paymentViewModel.uiEvent.collect { event ->
                    when (event) {
                        is woowacourse.shopping.ui.payment.PaymentUiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                        woowacourse.shopping.ui.payment.PaymentUiEvent.NavigateToProductList ->
                            navController.navigate(ProductListRoute) {
                                popUpTo<PaymentRoute> {
                                    inclusive = true
                                }
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
                    viewModel = paymentViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onClose = { navController.popBackStack() },
                    onPayClick = paymentViewModel::onClickPay,
                )
            }
        }
    }
}
