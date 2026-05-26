package woowacourse.shopping.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import woowacourse.shopping.AppContainer
import woowacourse.shopping.data.alarm.PayReminderAlarm
import woowacourse.shopping.data.alarm.PayReminderPreference
import woowacourse.shopping.ui.cart.CartEvent
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.detail.DetailEvent
import woowacourse.shopping.ui.detail.DetailScreen
import woowacourse.shopping.ui.detail.DetailViewModel
import woowacourse.shopping.ui.pay.PayEvent
import woowacourse.shopping.ui.pay.PayScreen
import woowacourse.shopping.ui.pay.PayViewModel
import woowacourse.shopping.ui.recommend.RecommendEvent
import woowacourse.shopping.ui.recommend.RecommendScreen
import woowacourse.shopping.ui.recommend.RecommendViewModel
import woowacourse.shopping.ui.setting.SettingScreen
import woowacourse.shopping.ui.shopping.ShoppingScreen
import woowacourse.shopping.ui.shopping.ShoppingViewModel
import woowacourse.shopping.ui.util.customToastMessage

@Composable
fun ShoppingNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    shouldOpenPayScreen: Boolean = false,
    onOpenPayScreen: () -> Unit = {},
) {
    val navController = rememberNavController()

    LaunchedEffect(shouldOpenPayScreen) {
        if (shouldOpenPayScreen) {
            navController.navigate(ShoppingRoute.Pay) {
                launchSingleTop = true
            }
            onOpenPayScreen()
        }
    }

    NavHost(
        navController = navController,
        startDestination = ShoppingRoute.Shopping,
        modifier = modifier,
    ) {
        composable<ShoppingRoute.Shopping> {
            ShoppingRouteContent(
                appContainer = appContainer,
                onProductClick = { productId ->
                    navController.navigate(ShoppingRoute.Detail(productId = productId))
                },
                onCartClick = {
                    navController.navigate(ShoppingRoute.Cart)
                },
                onSettingClick = {
                    navController.navigate(ShoppingRoute.Setting)
                },
            )
        }

        composable<ShoppingRoute.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<ShoppingRoute.Detail>()

            DetailRouteContent(
                appContainer = appContainer,
                productId = route.productId,
                hideRecentItem = route.hideRecentItem,
                onBackToShopping = {
                    navController.popBackStack(
                        route = ShoppingRoute.Shopping,
                        inclusive = false,
                    )
                },
                onNavigateToCart = {
                    navController.navigate(ShoppingRoute.Cart)
                },
                onRecentItemClick = { productId ->
                    navController.navigate(
                        ShoppingRoute.Detail(
                            productId = productId,
                            hideRecentItem = true,
                        ),
                    )
                },
            )
        }

        composable<ShoppingRoute.Cart> {
            CartRouteContent(
                appContainer = appContainer,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToRecommend = {
                    navController.navigate(ShoppingRoute.Recommend)
                },
            )
        }

        composable<ShoppingRoute.Recommend> {
            RecommendRouteContent(
                appContainer = appContainer,
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderClick = {
                    navController.navigate(ShoppingRoute.Pay)
                },
            )
        }

        composable<ShoppingRoute.Pay> {
            PayRouteContent(
                appContainer = appContainer,
                onBackClick = {
                    navController.popBackStack()
                },
                onPayComplete = {
                    navController.navigate(ShoppingRoute.Shopping) {
                        popUpTo(ShoppingRoute.Shopping) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<ShoppingRoute.Setting> {
            SettingRouteContent(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
private fun ShoppingRouteContent(
    appContainer: AppContainer,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onSettingClick: () -> Unit,
) {
    val viewModel: ShoppingViewModel =
        viewModel(
            factory =
                ShoppingViewModel.provideFactory(
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentItemRepository = appContainer.recentItemRepository,
                    networkObserver = appContainer.networkObserver,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShoppingScreen(
        uiState = uiState,
        onLoad = viewModel::loadMore,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onSettingClick = onSettingClick,
        onQuantityChange = viewModel::updateQuantity,
    )
}

@Composable
private fun DetailRouteContent(
    appContainer: AppContainer,
    productId: String,
    hideRecentItem: Boolean,
    onBackToShopping: () -> Unit,
    onNavigateToCart: () -> Unit,
    onRecentItemClick: (String) -> Unit,
) {
    val context = LocalContext.current

    BackHandler {
        onBackToShopping()
    }

    val viewModel: DetailViewModel =
        viewModel(
            factory =
                DetailViewModel.provideFactory(
                    id = productId,
                    hideRecentItem = hideRecentItem,
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentItemRepository = appContainer.recentItemRepository,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                DetailEvent.NavigateToCart -> {
                    onNavigateToCart()
                }

                DetailEvent.NavigateBack -> {
                    onBackToShopping()
                }

                DetailEvent.ShowProductNotFoundMessage -> {
                    customToastMessage(
                        context,
                        "?곹뭹??李얠쓣 ???놁뒿?덈떎.",
                    )
                }

                DetailEvent.ShowProductLoadFailureMessage -> {
                    customToastMessage(
                        context,
                        "?곹뭹 ?뺣낫瑜?遺덈윭?ㅼ? 紐삵뻽?듬땲??",
                    )
                }

                DetailEvent.ShowAddCartFailureMessage -> {
                    customToastMessage(
                        context,
                        "?λ컮援щ땲???곹뭹???댁? 紐삵뻽?듬땲??",
                    )
                }
            }
        }
    }

    DetailScreen(
        uiState = uiState,
        onCloseClick = onBackToShopping,
        onQuantityChange = viewModel::updateQuantity,
        onAddToCart = viewModel::addToCart,
        onRecentItemClick = onRecentItemClick,
    )
}

@Composable
private fun CartRouteContent(
    appContainer: AppContainer,
    onBackClick: () -> Unit,
    onNavigateToRecommend: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: CartViewModel =
        viewModel(
            factory =
                CartViewModel.provideFactory(
                    cartRepository = appContainer.cartRepository,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                CartEvent.DeleteCartItemFailure -> {
                    customToastMessage(context, "?λ컮援щ땲 ?곹뭹????젣?섏? 紐삵뻽?듬땲??")
                }

                CartEvent.UpdateCartItemFailure -> {
                    customToastMessage(context, "?λ컮援щ땲 ?곹뭹 ?섎웾??蹂寃쏀븯吏 紐삵뻽?듬땲??")
                }

                CartEvent.NavigateToRecommend -> {
                    onNavigateToRecommend()
                }
            }
        }
    }

    CartScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDeleteItem = viewModel::deleteItem,
        onNextPage = viewModel::nextPage,
        onPreviousPage = viewModel::previousPage,
        onQuantityChange = viewModel::updateQuantity,
        onCheckedChange = viewModel::checkItem,
        isAllSelectClick = viewModel::isAllSelectClick,
        onOrderClick = viewModel::order,
    )
}

@Composable
private fun RecommendRouteContent(
    appContainer: AppContainer,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: RecommendViewModel =
        viewModel(
            factory =
                RecommendViewModel.provideFactory(
                    cartRepository = appContainer.cartRepository,
                    recentItemRepository = appContainer.recentItemRepository,
                    productRepository = appContainer.productRepository,
                ),
        )

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                RecommendEvent.UpdateCartItemFailure -> {
                    customToastMessage(context, "?λ컮援щ땲 ?곹뭹??蹂寃쏀븯吏 紐삵뻽?듬땲??")
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecommendScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onOrderClick = onOrderClick,
        onQuantityChange = viewModel::updateQuantity,
    )
}

@Composable
private fun PayRouteContent(
    appContainer: AppContainer,
    onBackClick: () -> Unit,
    onPayComplete: () -> Unit,
) {
    val context = LocalContext.current
    val payReminderAlarm =
        remember {
            PayReminderAlarm(context)
        }
    val payReminderPreference =
        remember {
            PayReminderPreference(context)
        }

    LaunchedEffect(Unit) {
        payReminderAlarm.cancel()

        val isEnabled = payReminderPreference.isEnabled()

        if (isEnabled) {
            payReminderAlarm.schedule()
        }
    }

    val viewModel: PayViewModel =
        viewModel(
            factory =
                PayViewModel.provideFactory(
                    couponRepository = appContainer.couponRepository,
                    cartRepository = appContainer.cartRepository,
                ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                PayEvent.NavigateToShopping -> {
                    payReminderAlarm.cancel()
                    onPayComplete()
                }

                PayEvent.CompletePayFailure -> {
                    customToastMessage(context, "寃곗젣瑜??꾨즺?섏? 紐삵뻽?듬땲??")
                }
            }
        }
    }

    PayScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPayClick = viewModel::completePay,
        onCouponClick = viewModel::selectCoupon,
    )
}

@Composable
private fun SettingRouteContent(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val payReminderPreference =
        remember {
            PayReminderPreference(context)
        }
    val isNotificationEnabledFlow =
        remember {
            MutableStateFlow(payReminderPreference.isEnabled())
        }
    val isNotificationEnabled by isNotificationEnabledFlow.collectAsStateWithLifecycle()

    SettingScreen(
        isNotificationEnabled = isNotificationEnabled,
        onBackClick = onBackClick,
        onToggleClick = { isEnabled ->
            isNotificationEnabledFlow.value = isEnabled
            payReminderPreference.setEnabled(isEnabled)
        },
    )
}
