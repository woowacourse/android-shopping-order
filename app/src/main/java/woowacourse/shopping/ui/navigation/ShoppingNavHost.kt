package woowacourse.shopping.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.AppContainer
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.component.CustomToastMessage
import woowacourse.shopping.ui.detail.DetailEvent
import woowacourse.shopping.ui.detail.DetailScreen
import woowacourse.shopping.ui.detail.DetailViewModel
import woowacourse.shopping.ui.recommend.RecommendScreen
import woowacourse.shopping.ui.recommend.RecommendViewModel
import woowacourse.shopping.ui.shopping.ShoppingScreen
import woowacourse.shopping.ui.shopping.ShoppingViewModel

@Composable
fun ShoppingNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

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
                            CustomToastMessage(
                                context,
                                "상품을 찾을 수 없습니다.",
                            )
                        }

                        DetailEvent.ShowProductLoadFailureMessage -> {
                            CustomToastMessage(
                                context,
                                "상품 정보를 불러오지 못했습니다.",
                            )
                        }

                        DetailEvent.ShowAddCartFailureMessage -> {
                            CustomToastMessage(
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
            val viewModel: CartViewModel =
                viewModel(
                    factory =
                        CartViewModel.provideFactory(
                            cartRepository = appContainer.cartRepository,
                        ),
                )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                onOrderClick = {
                    navController.navigate(ShoppingRoute.Recommend)
                },
            )
        }

        composable<ShoppingRoute.Recommend> {
            val viewModel: RecommendViewModel =
                viewModel(
                    factory =
                        RecommendViewModel.provideFactory(
                            cartRepository = appContainer.cartRepository,
                            recentItemRepository = appContainer.recentItemRepository,
                            productRepository = appContainer.productRepository,
                        ),
                )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            RecommendScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onQuantityChange = viewModel::updateQuantity,
            )
        }
    }
}
