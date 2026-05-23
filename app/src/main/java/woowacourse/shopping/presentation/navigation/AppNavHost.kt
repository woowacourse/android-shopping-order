package woowacourse.shopping.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.presentation.cart.CartRoute
import woowacourse.shopping.presentation.detail.DetailRoute
import woowacourse.shopping.presentation.payment.PaymentRoute
import woowacourse.shopping.presentation.recommend.RecommendRoute
import woowacourse.shopping.presentation.setting.SettingRoute
import woowacourse.shopping.presentation.shopping.ShoppingRoute
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(startDestinationFromNotification: Any? = null) {
    val navController = rememberNavController()
    val settingRepository = remember { RepositoryProvider.settingRepository }
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            settingRepository.setPaymentPendingNotificationEnabled(isGranted)
        }

    LaunchedEffect(startDestinationFromNotification) {
        if (startDestinationFromNotification != null) {
            navController.navigate(startDestinationFromNotification)
        }
    }

    LaunchedEffect(Unit) {
        if (!settingRepository.hasAskedNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val alreadyGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED
                if (!alreadyGranted) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    settingRepository.setPaymentPendingNotificationEnabled(true)
                }
            } else {
                settingRepository.setPaymentPendingNotificationEnabled(true)
            }
            settingRepository.markNotificationPermissionAsked()
        }
    }

    NavHost(
        navController = navController,
        startDestination = ShoppingScreen,
    ) {
        composable<ShoppingScreen> {
            ShoppingRoute(navController = navController)
        }

        composable<DetailScreen> { backStackEntry ->
            val route: DetailScreen = backStackEntry.toRoute()

            DetailRoute(
                productId = route.productId,
                isFromLastSeen = route.isFromLastSeen,
                navController = navController,
            )
        }

        composable<CartScreen> {
            CartRoute(
                navController = navController,
            )
        }

        composable<RecommendScreen> { backStackEntry ->
            val route: RecommendScreen = backStackEntry.toRoute()

            RecommendRoute(
                productIds = route.productIds,
                navController = navController,
            )
        }

        composable<PaymentScreen>(
            typeMap =
                mapOf(
                    typeOf<List<OrderItem>>() to OrderItemListType,
                ),
        ) { backStackEntry ->
            val route: PaymentScreen = backStackEntry.toRoute()

            PaymentRoute(
                orderItems = route.orderItems,
                orderAmount = route.orderAmount,
                navController = navController,
            )
        }

        composable<SettingScreen> {
            SettingRoute(
                onBack = navController::popBackStack,
            )
        }
    }
}
