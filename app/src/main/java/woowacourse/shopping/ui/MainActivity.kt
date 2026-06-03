package woowacourse.shopping.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.CartRoute
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.CartViewModelFactory
import woowacourse.shopping.ui.navigation.Cart
import woowacourse.shopping.ui.navigation.Payment
import woowacourse.shopping.ui.navigation.ProductDetail
import woowacourse.shopping.ui.navigation.Recommendation
import woowacourse.shopping.ui.navigation.Shopping
import woowacourse.shopping.ui.payment.PaymentRoute
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.ui.payment.PaymentViewModelFactory
import woowacourse.shopping.ui.productdetail.ProductDetailRoute
import woowacourse.shopping.ui.productdetail.ProductDetailViewModel
import woowacourse.shopping.ui.productdetail.ProductDetailViewModelFactory
import woowacourse.shopping.ui.recommendation.RecommendationRoute
import woowacourse.shopping.ui.recommendation.RecommendationViewModel
import woowacourse.shopping.ui.recommendation.RecommendationViewModelFactory
import woowacourse.shopping.ui.shopping.ShoppingRoute
import woowacourse.shopping.ui.shopping.ShoppingViewModel
import woowacourse.shopping.ui.shopping.ShoppingViewModelFactory
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            (application as ShoppingApplication).userDataSource.setNotificationEnable(isGranted)
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val app = application as ShoppingApplication

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Shopping,
                    ) {
                        composable<Shopping> {
                            val viewModel: ShoppingViewModel =
                                viewModel(
                                    factory =
                                        ShoppingViewModelFactory(
                                            cartRepository = app.cartRepository,
                                            recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                            productRepository = app.productRepository,
                                            userDataSource = app.userDataSource,
                                        ),
                                )
                            ShoppingRoute(
                                viewModel = viewModel,
                                onNavigateToCart = { navController.navigate(Cart) },
                                onNavigateToProductDetail = { selectedId, lastId ->
                                    navController.navigate(
                                        ProductDetail(
                                            selectedProductId = selectedId,
                                            lastViewedProductId = lastId,
                                        ),
                                    )
                                },
                                modifier = Modifier.padding(innerPadding),
                            )
                        }

                        composable<ProductDetail> { backStackEntry ->
                            val route: ProductDetail = backStackEntry.toRoute()

                            val viewModel: ProductDetailViewModel =
                                viewModel(
                                    factory =
                                        ProductDetailViewModelFactory(
                                            cartRepository = app.cartRepository,
                                            recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                            productRepository = app.productRepository,
                                            selectedProductId = route.selectedProductId,
                                            lastViewedProductId = route.lastViewedProductId,
                                        ),
                                )
                            ProductDetailRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }

                        composable<Cart> {
                            val viewModel: CartViewModel =
                                viewModel(
                                    factory =
                                        CartViewModelFactory(
                                            cartRepository = app.cartRepository,
                                        ),
                                )
                            CartRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }

                        composable<Recommendation> { backStackEntry ->
                            val route: Recommendation = backStackEntry.toRoute()

                            val viewModel: RecommendationViewModel =
                                viewModel(
                                    factory =
                                        RecommendationViewModelFactory(
                                            cartRepository = app.cartRepository,
                                            productRepository = app.productRepository,
                                            recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                            outstandingProductRepository = app.outstandingProductRepository,
                                            initPrice = route.totalPrice,
                                            initCheckItemIds = route.checkedIds,
                                        ),
                                )
                            RecommendationRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }

                        composable<Payment>(
                            deepLinks = listOf(
                                navDeepLink { uriPattern = "shopping://payment" }
                            )
                        ) {
                            val viewModel: PaymentViewModel =
                                viewModel(
                                    factory =
                                        PaymentViewModelFactory(
                                            cartRepository = app.cartRepository,
                                            couponRepository = app.couponRepository,
                                            orderRepository = app.orderRepository,
                                            outstandingProductRepository = app.outstandingProductRepository,
                                            userDataSource = app.userDataSource,
                                            alarmScheduler = ShoppingApplication.alarmScheduler,
                                        ),
                                )
                            PaymentRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }
                }
            }
        }
        checkNotificationPermission()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
