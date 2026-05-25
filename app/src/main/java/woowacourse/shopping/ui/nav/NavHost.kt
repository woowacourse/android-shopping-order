package woowacourse.shopping.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.data.remote.NetworkMonitor
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.productdetail.ProductDetailViewModel
import woowacourse.shopping.ui.shopping.ShoppingScreen
import woowacourse.shopping.ui.shopping.ShoppingViewModel

@Composable
fun AppNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Shopping,
        modifier = modifier,
    ) {
        composable<Shopping> {
            val viewModel: ShoppingViewModel =
                viewModel(
                    factory = ShoppingViewModel.provideFactory(
                        container = container,
                        networkMonitor = NetworkMonitor(context),
                        loadSize = 20,
                    ),
                )

            ShoppingScreen(
                viewModel = viewModel,
                onCartClick = { navController.navigate(Cart) },
                onProductClick = { product ->
                    navController.navigate(ProductDetail(productId = product.id))
                },
                onRecentProductClick = { product ->
                    navController.navigate(ProductDetail(productId = product.id))
                },
            )
        }

        composable<ProductDetail> {
            val viewModel: ProductDetailViewModel =
                viewModel(
                    factory = ProductDetailViewModel.provideFactory(
                        container = container,
                    ),
                )

            ProductDetailScreen(
                viewModel = viewModel,
                onCloseClick = { navController.popBackStack() },
                onAddToCartClick = { navController.popBackStack() },
                onLastViewedProductClick = { product ->
                    navController.navigate(ProductDetail(productId = product.id, isFromBanner = true)) {
                        popUpTo(Shopping) { saveState = true }
                    }
                },
            )
        }

        composable<Cart> {
            val viewModel: CartViewModel =
                viewModel(
                    factory = CartViewModel.provideFactory(container = container),
                )

            CartScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onOrderClick = { cartItemIds ->
                    navController.navigate(Payment(cartItemIds = cartItemIds.joinToString(",")))
                },
            )
        }

        composable<Payment> { backStackEntry ->
            val payment = backStackEntry.toRoute<Payment>()
            val cartItemIds =
                payment.cartItemIds
                    .split(",")
                    .mapNotNull { it.toLongOrNull() }
            val viewModel: PaymentViewModel =
                viewModel(
                    factory =
                        PaymentViewModel.provideFactory(
                            container = container,
                            cartItemIds = cartItemIds,
                        ),
                )

            PaymentScreen(
                viewModel = viewModel,
                onCloseClick = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Shopping) {
                        popUpTo(Shopping) { inclusive = true }
                    }
                },
            )
        }
    }
}
