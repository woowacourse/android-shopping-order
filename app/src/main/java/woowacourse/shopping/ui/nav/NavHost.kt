package woowacourse.shopping.ui.nav

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.data.remote.NetworkMonitor
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.data.model.Product
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
    paymentCartItemIds: List<Long>? = null,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var notificationEnabled by remember { mutableStateOf(container.notificationRepository.isEnabled()) }
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            notificationEnabled = isGranted
            container.notificationRepository.setEnabled(isGranted)
        }
    val onNotificationEnabledChange: (Boolean) -> Unit = onNotificationEnabledChange@{ enabled ->
        if (!enabled) {
            notificationEnabled = false
            container.notificationRepository.setEnabled(false)
            return@onNotificationEnabledChange
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationEnabled = true
            container.notificationRepository.setEnabled(true)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(paymentCartItemIds) {
        if (!paymentCartItemIds.isNullOrEmpty()) {
            navController.navigate(Payment(cartItemIds = paymentCartItemIds.joinToString(","))) {
                launchSingleTop = true
            }
        }
    }

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
                onProductClick = { product: Product ->
                    navController.navigate(ProductDetail(productId = product.id))
                },
                onRecentProductClick = { product: Product ->
                    navController.navigate(ProductDetail(productId = product.id))
                },
                notificationEnabled = notificationEnabled,
                onNotificationEnabledChange = onNotificationEnabledChange,
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
