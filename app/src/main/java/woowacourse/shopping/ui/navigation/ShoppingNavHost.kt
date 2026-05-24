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
import woowacourse.shopping.AppContainer
import woowacourse.shopping.data.alarm.PayReminderAlarm
import woowacourse.shopping.data.alarm.PayReminderPreference
import woowacourse.shopping.ui.cart.CartEvent
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.detail.DetailEvent
import woowacourse.shopping.ui.detail.DetailScreen
import woowacourse.shopping.ui.detail.DetailViewModel
import woowacourse.shopping.ui.pay.PayScreen
import woowacourse.shopping.ui.pay.PayViewModel
import woowacourse.shopping.ui.recommend.RecommendEvent
import woowacourse.shopping.ui.recommend.RecommendScreen
import woowacourse.shopping.ui.recommend.RecommendViewModel
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
                onProductClick = { productId ->
                    navController.navigate(ShoppingRoute.Detail(productId = productId))
                },
                onCartClick = {
                    navController.navigate(ShoppingRoute.Cart)
                },
                onQuantityChange = viewModel::updateQuantity,
            )
        }

        composable<ShoppingRoute.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<ShoppingRoute.Detail>()
            val context = LocalContext.current
            BackHandler {
                navController.popBackStack(
                    route = ShoppingRoute.Shopping,
                    inclusive = false,
                )
            }
            val viewModel: DetailViewModel =
                viewModel(
                    factory =
                        DetailViewModel.provideFactory(
                            id = route.productId,
                            hideRecentItem = route.hideRecentItem,
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
                            navController.navigate(ShoppingRoute.Cart)
                        }

                        DetailEvent.NavigateBack -> {
                            navController.popBackStack(
                                route = ShoppingRoute.Shopping,
                                inclusive = false,
                            )
                        }

                        DetailEvent.ShowProductNotFoundMessage -> {
                            customToastMessage(
                                context,
                                "상품을 찾을 수 없습니다.",
                            )
                        }

                        DetailEvent.ShowProductLoadFailureMessage -> {
                            customToastMessage(
                                context,
                                "상품 정보를 불러오지 못했습니다.",
                            )
                        }

                        DetailEvent.ShowAddCartFailureMessage -> {
                            customToastMessage(
                                context,
                                "장바구니에 상품을 담지 못했습니다.",
                            )
                        }
                    }
                }
            }

            DetailScreen(
                uiState = uiState,
                onCloseClick = {
                    navController.popBackStack(
                        route = ShoppingRoute.Shopping,
                        inclusive = false,
                    )
                },
                onQuantityChange = viewModel::updateQuantity,
                onAddToCart = viewModel::addToCart,
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
                            customToastMessage(context, "장바구니 상품을 삭제하지 못했습니다.")
                        }

                        CartEvent.UpdateCartItemFailure -> {
                            customToastMessage(context, "장바구니 상품 수량을 변경하지 못했습니다.")
                        }

                        CartEvent.NavigateToRecommend -> {
                            navController.navigate(ShoppingRoute.Recommend)
                        }
                    }
                }
            }

            CartScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onDeleteItem = viewModel::deleteItem,
                onNextPage = viewModel::nextPage,
                onPreviousPage = viewModel::previousPage,
                onQuantityChange = viewModel::updateQuantity,
                onCheckedChange = viewModel::checkItem,
                isAllSelectClick = viewModel::isAllSelectClick,
                onOrderClick = viewModel::order,
            )
        }

        composable<ShoppingRoute.Recommend> {
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
                            customToastMessage(context, "장바구니 상품을 변경하지 못했습니다.")
                        }
                    }
                }
            }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            RecommendScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderClick = {
                    navController.navigate(ShoppingRoute.Pay)
                },
                onQuantityChange = viewModel::updateQuantity,
            )
        }

        composable<ShoppingRoute.Pay> {
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

                if (payReminderPreference.isEnabled()) {
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

            PayScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onPayClick = {
                    payReminderAlarm.cancel()

                    navController.navigate(ShoppingRoute.Shopping) {
                        popUpTo(ShoppingRoute.Shopping) {
                            inclusive = false
                        }
                    }
                },
                onCouponClick = viewModel::selectCoupon,
            )
        }
    }
}
