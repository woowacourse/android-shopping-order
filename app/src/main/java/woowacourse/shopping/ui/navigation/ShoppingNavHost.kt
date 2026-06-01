package woowacourse.shopping.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import woowacourse.shopping.domain.notification.PaymentNotificationScheduler
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.ui.cart.cartRoute
import woowacourse.shopping.ui.catalog.catalogRoute
import woowacourse.shopping.ui.payment.paymentRoute
import woowacourse.shopping.ui.productdetail.productDetailRoute
import woowacourse.shopping.ui.recommendation.recommendationRoute
import woowacourse.shopping.ui.settings.settingRoute

@Composable
fun ShoppingNavHost(
    navController: NavHostController,
    cartRepository: CartRepository,
    couponRepository: CouponRepository,
    orderRepository: OrderRepository,
    productRepository: ProductRepository,
    recentlyViewedProductRepository: RecentlyViewedProductRepository,
    settingRepository: SettingRepository,
    paymentNotificationScheduler: PaymentNotificationScheduler,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingRoute.Catalog,
        modifier = modifier,
    ) {
        catalogRoute(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentlyViewedProductRepository = recentlyViewedProductRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onProductClick = { selectedProductId, lastViewedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = lastViewedProductId,
                    ),
                )
            },
            onCartClick = {
                navController.navigate(
                    ShoppingRoute.Cart,
                )
            },
            onSettingsClick = {
                navController.navigate(
                    ShoppingRoute.Setting,
                )
            },
        )

        productDetailRoute(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentlyViewedProductRepository = recentlyViewedProductRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onLastViewedProductClick = { selectedProductId, lastViewedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = lastViewedProductId,
                    ),
                )
            },
            onBackClick = {
                navController.popBackStack()
            },
        )

        cartRoute(
            cartRepository = cartRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = { navController.popBackStack() },
            onOrderClick = { selectedCartItemIds ->
                navController.navigate(
                    ShoppingRoute.Recommendation(
                        selectedCartItemIds = selectedCartItemIds,
                    ),
                )
            },
        )

        recommendationRoute(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentlyViewedProductRepository = recentlyViewedProductRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onItemClick = { selectedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = null,
                    ),
                )
            },
            onOrderClick = { selectedCartItemIds ->
                navController.navigate(
                    ShoppingRoute.Payment(
                        selectedCartItemIds = selectedCartItemIds,
                    ),
                )
            },
            onBackClick = { navController.popBackStack() },
        )

        paymentRoute(
            cartRepository = cartRepository,
            couponRepository = couponRepository,
            orderRepository = orderRepository,
            paymentNotificationScheduler = paymentNotificationScheduler,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = { navController.popBackStack() },
            onPaymentComplete = {
                navController.navigate(ShoppingRoute.Catalog) {
                    popUpTo(ShoppingRoute.Catalog) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )

        settingRoute(
            settingRepository = settingRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = { navController.popBackStack() },
        )
    }
}
