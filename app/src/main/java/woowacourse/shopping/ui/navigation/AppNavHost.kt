package woowacourse.shopping.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import kotlinx.coroutines.launch
import woowacourse.shopping.local.SettingsPreferences
import woowacourse.shopping.receiver.AlarmHelper
import woowacourse.shopping.ui.cart.list.CartScreen
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.list.CartViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendedProductsScreen
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.ui.payment.PaymentViewModelFactory
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.productdetail.ProductDetailViewModel
import woowacourse.shopping.ui.productdetail.ProductDetailViewModelFactory
import woowacourse.shopping.ui.settings.SettingsScreen
import woowacourse.shopping.ui.settings.SettingsViewModel
import woowacourse.shopping.ui.shopping.ShoppingScreen
import woowacourse.shopping.ui.shopping.ShoppingViewModel
import woowacourse.shopping.ui.shopping.ShoppingViewModelFactory
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            // Permission result handled if needed
        }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ProductList,
    ) {
        composable<ProductList> {
            val viewModel: ShoppingViewModel =
                viewModel(
                    factory = ShoppingViewModelFactory(),
                )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                viewModel.reloadVisibleState()
            }

            ShoppingScreen(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
                onCartClick = { navController.navigate(Cart) },
                onSettingsClick = { navController.navigate(Settings) },
                onMoreClick = viewModel::loadMore,
                onAddToCart = viewModel::addToCart,
                onIncreaseQuantity = viewModel::increaseQuantity,
                onDecreaseQuantity = viewModel::decreaseQuantity,
            )
        }

        composable<Settings> {
            val settingsPreferences = remember { SettingsPreferences(context) }
            val viewModel: SettingsViewModel =
                viewModel(
                    factory = SettingsViewModel.factory(settingsPreferences),
                )

            val isPaymentReminderEnabled by viewModel.isPaymentReminderEnabled.collectAsStateWithLifecycle()

            SettingsScreen(
                isPaymentReminderEnabled = isPaymentReminderEnabled,
                onTogglePaymentReminder = viewModel::togglePaymentReminder,
                onBackClick = { navController.popBackStack() },
                modifier = Modifier.padding(innerPadding),
            )
        }

        composable<ProductDetail> {
            val viewModel: ProductDetailViewModel =
                viewModel(
                    factory = ProductDetailViewModelFactory(),
                )
            val snackbarHostState = remember { SnackbarHostState() }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.snackbarEvent.collect { message ->
                    snackbarHostState.showSnackbar(message)
                }
            }

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { innerPadding ->
                ProductDetailScreen(
                    uiState = uiState,
                    modifier = Modifier.padding(innerPadding),
                    onCloseClick = { navController.popBackStack() },
                    onLastViewedProductClick = { productId ->
                        navController.navigate(ProductDetail(productId)) {
                            popUpTo<ProductDetail> {
                                inclusive = true
                            }
                        }
                    },
                    onAddToCart = viewModel::addToCart,
                    onIncreaseQuantity = viewModel::increaseQuantity,
                    onDecreaseQuantity = viewModel::decreaseQuantity,
                )
            }
        }

        composable<Cart> {
            val viewModel: CartViewModel =
                viewModel(
                    factory = CartViewModelFactory(),
                )
            val snackbarHostState = remember { SnackbarHostState() }

            val coroutineScope = rememberCoroutineScope()

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.snackbarEvent.collect { message ->
                    snackbarHostState.showSnackbar(message)
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                viewModel.reloadVisibleState()
            }

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { innerPadding ->
                CartScreen(
                    uiState = uiState,
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = {
                        coroutineScope.launch {
                            val orderProducts = viewModel.getSelectedOrderProducts()
                            if (orderProducts.isNotEmpty()) {
                                navController.navigate(CartRecommendation(orderProducts))
                            }
                        }
                    },
                    onItemCheckedChange = viewModel::toggleItemSelection,
                    onAllCheckedChange = viewModel::toggleAllSelection,
                    onDeleteClick = viewModel::delete,
                    onIncreaseQuantity = viewModel::increaseQuantity,
                    onDecreaseQuantity = viewModel::decreaseQuantity,
                    onPreviousClick = viewModel::loadPreviousPage,
                    onNextClick = viewModel::loadNextPage,
                )
            }
        }

        composable<CartRecommendation>(
            typeMap = mapOf(typeOf<List<OrderProduct>>() to OrderProductListType),
        ) {
            val cartViewModel: CartViewModel =
                viewModel(
                    factory = CartViewModelFactory(),
                )
            val recommendationViewModel: CartRecommendationViewModel =
                viewModel(
                    factory = CartRecommendationViewModelFactory(),
                )

            val uiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.orderProductsToOrder) {
                val orderProducts = uiState.orderProductsToOrder
                if (orderProducts != null) {
                    navController.navigate(Payment(orderProducts = orderProducts)) {
                        popUpTo<CartRecommendation> {
                            inclusive = true
                        }
                    }
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                cartViewModel.reloadVisibleState()
                recommendationViewModel.reloadVisibleState()
            }

            CartRecommendedProductsScreen(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
                onAddToCart = recommendationViewModel::addRecommendedProduct,
                onIncreaseQuantity = recommendationViewModel::addRecommendedProduct,
                onDecreaseQuantity = recommendationViewModel::decreaseRecommendedProductQuantity,
                onOrderClick = recommendationViewModel::applyRecommendations,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<Payment>(
            deepLinks =
                listOf(
                    navDeepLink { uriPattern = "shopping://payment" },
                ),
            typeMap = mapOf(typeOf<List<OrderProduct>>() to OrderProductListType),
        ) {
            val viewModel: PaymentViewModel =
                viewModel(
                    factory = PaymentViewModelFactory(),
                )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                AlarmHelper.schedulePaymentReminder(context)
            }

            LaunchedEffect(uiState.isOrderCompleted) {
                if (uiState.isOrderCompleted) {
                    AlarmHelper.cancelPaymentReminder(context)
                    navController.navigate(ProductList) {
                        popUpTo<ProductList> {
                            inclusive = true
                        }
                    }
                }
            }

            PaymentScreen(
                uiState = uiState,
                onBackClick = {
                    AlarmHelper.cancelPaymentReminder(context)
                    navController.popBackStack()
                },
                onCouponCheckedChange = { couponId, _ ->
                    viewModel.selectCoupon(couponId)
                },
                onPaymentClick = viewModel::pay,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
