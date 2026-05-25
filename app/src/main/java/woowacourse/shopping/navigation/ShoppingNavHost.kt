package woowacourse.shopping.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.viewmodel.OrderViewModel
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderItem
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.navigation.Route.Cart
import woowacourse.shopping.navigation.Route.Coupon
import woowacourse.shopping.navigation.Route.ProductDetail
import woowacourse.shopping.navigation.Route.ProductList
import woowacourse.shopping.notification.PaymentReminderAlarmScheduler
import woowacourse.shopping.storage.sharedpreferences.NotificationPreferenceRepository
import woowacourse.shopping.ui.component.cart.PageNavigation
import woowacourse.shopping.ui.component.cart.ShoppingCartOrderButton
import woowacourse.shopping.ui.component.productlist.MoreButton
import woowacourse.shopping.ui.screen.CouponScreen
import woowacourse.shopping.ui.screen.DetailProductScreen
import woowacourse.shopping.ui.screen.ProductListScreen
import woowacourse.shopping.ui.screen.ShoppingCartRecommendSection
import woowacourse.shopping.ui.screen.ShoppingCartScreen
import woowacourse.shopping.ui.state.ShoppingCartState
import woowacourse.shopping.ui.viewmodel.CouponViewModel
import woowacourse.shopping.ui.viewmodel.DetailProductEvent
import woowacourse.shopping.ui.viewmodel.DetailProductViewModel
import woowacourse.shopping.ui.viewmodel.ProductListViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingCartEvent
import woowacourse.shopping.ui.viewmodel.ShoppingCartItemViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingCartRecommendViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingCartRecommendViewModel.ShoppingCartStep

private fun navigateToCoupon(
    navController: NavHostController,
    couponViewModel: CouponViewModel,
    baseSelectedCartItemIds: Set<Long>,
    recommendedShoppingItems: List<ShoppingItem>,
    shoppingCartItems: List<ShoppingCartItem>,
) {
    val orderedCartItemIds =
        createOrderedCartItemIds(
            baseSelectedCartItemIds = baseSelectedCartItemIds,
            recommendedShoppingItems = recommendedShoppingItems,
            shoppingCartItems = shoppingCartItems,
        )

    if (orderedCartItemIds.isEmpty()) return

    couponViewModel.initialize(
        order =
            createOrder(
                orderedCartItemIds = orderedCartItemIds,
                shoppingCartItems = shoppingCartItems,
            ),
        orderedCartItemIds = orderedCartItemIds,
    )
    couponViewModel.loadCoupons()
    navController.navigate(Coupon)
}

private fun createOrderedCartItemIds(
    baseSelectedCartItemIds: Set<Long>,
    recommendedShoppingItems: List<ShoppingItem>,
    shoppingCartItems: List<ShoppingCartItem>,
): Set<Long> {
    val recommendedCartItemIds =
        resolveRecommendedCartItemIds(
            recommendedShoppingItems = recommendedShoppingItems,
            shoppingCartItems = shoppingCartItems,
        )

    return linkedSetOf<Long>().apply {
        addAll(baseSelectedCartItemIds)
        addAll(recommendedCartItemIds)
    }
}

private fun createOrder(
    orderedCartItemIds: Set<Long>,
    shoppingCartItems: List<ShoppingCartItem>,
): Order =
    Order(
        items =
            shoppingCartItems
                .filter { it.getId() in orderedCartItemIds }
                .map { it.toOrderItem() },
    )

private fun ShoppingCartItem.toOrderItem(): OrderItem =
    OrderItem(
        productId = product.id,
        unitPrice = Price(product.getPrice()),
        quantity = getQuantity(),
    )

private fun navigateToProductList(navController: NavHostController) {
    navController.navigate(ProductList) {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = false
        }
        launchSingleTop = true
    }
}

@Composable
fun ShoppingNavHost(
    modifier: Modifier,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    snackbarHostState: SnackbarHostState,
) {
    val shoppingCartViewModel: ShoppingCartItemViewModel = viewModel(factory = viewModelFactory)
    val shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel =
        viewModel(factory = viewModelFactory)
    val orderViewModel: OrderViewModel = viewModel(factory = viewModelFactory)
    val couponViewModel: CouponViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = ProductList,
    ) {
        composable<ProductList> { backStackEntry ->
            val productListViewModel: ProductListViewModel =
                viewModel(
                    viewModelStoreOwner = backStackEntry,
                    factory = viewModelFactory,
                )

            val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(productListViewModel) {
                productListViewModel.requestProducts(size = MAX_PRODUCT_SIZE)
            }

            LaunchedEffect(productListViewModel) {
                productListViewModel.event.collect { event ->
                    when (event) {
                        is ProductListViewModel.ProductListEvent.NavigateToDetailProduct -> {
                            navController.navigate(
                                ProductDetail(
                                    productId = event.productId,
                                    showLastViewed = event.showLastViewed,
                                ),
                            )
                        }

                        ProductListViewModel.ProductListEvent.NavigateToShoppingCart -> {
                            navController.navigate(Cart)
                        }
                    }
                }
            }

            ProductListScreen(
                shoppingItems = uiState.shoppingItems,
                recentViewedShoppingItems = uiState.recentViewedShoppingItems,
                shoppingCartTotalCount = uiState.shoppingCartTotalCount,
                isNetworkConnected = uiState.isNetworkConnected,
                state = uiState,
                onAddToCartClick = { shoppingItem ->
                    productListViewModel.addProductToCart(shoppingItem)
                },
                onQuantityPlusClick = { shoppingItem ->
                    productListViewModel.increaseProductQuantity(shoppingItem)
                },
                onQuantityMinusClick = { shoppingItem ->
                    productListViewModel.decreaseProductQuantity(shoppingItem)
                },
                onProductClick = productListViewModel::onProductClick,
                onRecentViewedProductClick = productListViewModel::onRecentViewedProductClick,
                onNavigateToCartClick = productListViewModel::onNavigateToCartClick,
                bottomContent =
                    if (uiState.canLoadNextPage) {
                        {
                            MoreButton(onClick = productListViewModel::loadNextPage)
                        }
                    } else {
                        null
                    },
            )
        }

        composable<ProductDetail> { backStackEntry ->

            val route = backStackEntry.toRoute<ProductDetail>()
            val productDetailViewModel: DetailProductViewModel =
                viewModel(
                    viewModelStoreOwner = backStackEntry,
                    factory = viewModelFactory,
                )
            LaunchedEffect(DetailProductViewModel) {
                productDetailViewModel.event.collect { event ->
                    when (event) {
                        DetailProductEvent.AddToCartSuccess -> {
                            navController.popBackStack()
                            snackbarHostState.showSnackbar("장바구니에 담았습니다.")
                        }

                        is DetailProductEvent.AddToCartFailure -> {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    }
                }
            }

            LaunchedEffect(route.productId, route.showLastViewed) {
                productDetailViewModel.initialize(
                    productId = route.productId,
                    showLastViewed = route.showLastViewed,
                )
                productDetailViewModel.loadProductDetail(route.productId)
            }

            val uiState by productDetailViewModel.uiState.collectAsStateWithLifecycle()
            val shoppingItem = uiState.shoppingItem
            if (shoppingItem != null) {
                DetailProductScreen(
                    shoppingItem = shoppingItem,
                    lastViewedShoppingItem = uiState.lastViewedShoppingItem,
                    onAddToCartClick = {
                        productDetailViewModel.addSelectedProductToCart()
                    },
                    onLastViewedProductClick = { selectedProductId ->
                        navController.navigate(
                            ProductDetail(
                                productId = selectedProductId,
                                showLastViewed = false,
                            ),
                        )
                    },
                    onBackClick = { navController.popBackStack() },
                    quantity = uiState.selectedQuantity,
                    quantityPrice = uiState.quantityPrice,
                    onQuantityPlusClick = productDetailViewModel::increaseSelectedQuantity,
                    onQuantityMinusClick = productDetailViewModel::decreaseSelectedQuantity,
                )
            }
        }

        composable<Cart> {
            LaunchedEffect(shoppingCartViewModel) {
                shoppingCartViewModel.event.collect { event ->
                    when (event) {
                        ShoppingCartEvent.NavigateBack -> {
                            navController.popBackStack()
                        }

                        ShoppingCartEvent.RemoveSuccess -> {
                            snackbarHostState.showSnackbar("장바구니에서 삭제했습니다.")
                        }

                        is ShoppingCartEvent.RemoveFailure -> {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    }
                }
            }

            LaunchedEffect(shoppingCartViewModel) {
                shoppingCartViewModel.requestCartItems()
            }

            val shoppingCartItems by shoppingCartViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val selectedCartItemIds by shoppingCartViewModel.selectedCartItemIds.collectAsStateWithLifecycle()
            val selectedItemCount = selectedCartItemIds.size
            val recommendUiState by shoppingCartRecommendViewModel.uiState.collectAsStateWithLifecycle()

            val uiState by shoppingCartViewModel.uiState.collectAsStateWithLifecycle()
            val isLoading = uiState.isLoading
            val errorMessage = uiState.errorMessage
            val hasApiError = errorMessage != null
            val visibleItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    shoppingCartItems.items
                }
            val visiblePagedItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    shoppingCartItems.pagedItems
                }
            LaunchedEffect(shoppingCartItems, selectedCartItemIds) {
                shoppingCartRecommendViewModel.updateCartSnapshot(
                    shoppingCartItems = shoppingCartItems.items,
                    selectedCartItemIds = selectedCartItemIds,
                )
            }
            val state =
                ShoppingCartState(
                    items = visibleItems,
                    selectedCartItemIds = selectedCartItemIds,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    currentPage = shoppingCartItems.currentPage,
                    selectedItemCount = selectedItemCount,
                    canOrder = selectedItemCount > 0 && !isLoading,
                    canMoveToPreviousPage =
                        if (hasApiError) false else shoppingCartItems.canMoveToPreviousPage,
                    canMoveToNextPage = if (hasApiError) false else shoppingCartItems.canMoveToNextPage,
                )

            BackHandler(enabled = recommendUiState.currentStep == ShoppingCartStep.RECOMMENT) {
                shoppingCartRecommendViewModel.moveToCart()
            }
            if (recommendUiState.currentStep == ShoppingCartStep.CART) {
                ShoppingCartScreen(
                    shoppingCartItems = visiblePagedItems,
                    getQuantityPrice = shoppingCartViewModel::getQuantityPrice,
                    state = state,
                    onBackClick = shoppingCartViewModel::onBackClick,
                    onRemoveShoppingItemClick = { shoppingCartItem ->
                        shoppingCartViewModel.removeShoppingItem(shoppingCartItem)
                    },
                    onToggleShoppingItemSelectionClick = { cartItemId, isSelected ->
                        shoppingCartViewModel.setShoppingCartItemSelection(
                            cartItemId = cartItemId,
                            isSelected = isSelected,
                        )
                    },
                    onIncreaseShoppingItemQuantityClick = { shoppingCartItem ->
                        shoppingCartViewModel.increaseShoppingItemQuantity(shoppingCartItem)
                    },
                    onDecreaseShoppingItemQuantityClick = { shoppingCartItem ->
                        shoppingCartViewModel.decreaseShoppingItemQuantity(shoppingCartItem)
                    },
                ) {
                    PageNavigation(
                        currentPage = shoppingCartItems.currentPage,
                        canMoveToPreviousPage = if (hasApiError) false else shoppingCartItems.canMoveToPreviousPage,
                        canMoveToNextPage = if (hasApiError) false else shoppingCartItems.canMoveToNextPage,
                        onBeforePageClick = shoppingCartViewModel::moveToPreviousPage,
                        onNextPageClick = shoppingCartViewModel::moveToNextPage,
                    )
                    ShoppingCartOrderButton(
                        shoppingCartItems = shoppingCartItems.items,
                        selectedCartItemIds = selectedCartItemIds,
                        shoppingCartSelectItemCount = selectedItemCount,
                        onOrderButtonClick = { selectedCartItemIds ->
                            if (selectedCartItemIds.isEmpty()) {
                                return@ShoppingCartOrderButton
                            }
                            if (recommendUiState.recommendedShoppingItems.isNotEmpty()) {
                                shoppingCartRecommendViewModel.moveToRecommend()
                                return@ShoppingCartOrderButton
                            }
                            navigateToCoupon(
                                navController = navController,
                                couponViewModel = couponViewModel,
                                baseSelectedCartItemIds = selectedCartItemIds.toSet(),
                                recommendedShoppingItems = recommendUiState.recommendedShoppingItems,
                                shoppingCartItems = shoppingCartItems.items,
                            )
                        },
                        checked = shoppingCartItems.items.isNotEmpty() && selectedItemCount == shoppingCartItems.items.size,
                        orderComplete = shoppingCartItems.items.isNotEmpty(),
                        totalPrice =
                            shoppingCartViewModel.getTotalPrice(
                                shoppingCartItems = shoppingCartItems.items,
                                selectedCartItemIds = selectedCartItemIds,
                            ),
                        onToggleShoppingItemSelectionClick = { cartItemIds, isSelected ->
                            shoppingCartViewModel.setShoppingCartItemsSelection(
                                cartItemIds = cartItemIds,
                                isSelected = isSelected,
                            )
                        },
                    )
                }
            } else {
                ShoppingCartRecommendSection(
                    recommendedShoppingItems = recommendUiState.recommendedShoppingItems,
                    baseSelectedCartItemCount = selectedCartItemIds.size,
                    totalPrice = recommendUiState.selectedCartTotalPrice + recommendUiState.selectedRecommendTotalPrice,
                    onBackClick = shoppingCartViewModel::onBackClick,
                    onOrderButtonClick = {
                        navigateToCoupon(
                            navController = navController,
                            couponViewModel = couponViewModel,
                            baseSelectedCartItemIds = selectedCartItemIds.toSet(),
                            recommendedShoppingItems = recommendUiState.recommendedShoppingItems,
                            shoppingCartItems = shoppingCartItems.items,
                        )
                    },
                    onAddToCartClick = { shoppingItem ->
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                        )
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                        )
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        shoppingCartViewModel.decreaseByProductId(
                            productId = shoppingItem.getProductId(),
                        )
                    },
                )
            }
        }
        composable<Coupon> {
            val context = LocalContext.current
            val notificationPreferenceRepository =
                remember(context) { NotificationPreferenceRepository(context) }
            var isNotificationEnabled by remember {
                mutableStateOf(notificationPreferenceRepository.isNotificationEnabled())
            }
            var shouldScheduleReminderOnExit by remember { mutableStateOf(true) }
            val notificationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { }

            LaunchedEffect(Unit) {
                isNotificationEnabled = notificationPreferenceRepository.isNotificationEnabled()
                shouldScheduleReminderOnExit = true
                PaymentReminderAlarmScheduler.cancel(context)
                if (isNotificationEnabled) {
                    requestNotificationPermissionIfNeeded(
                        context = context,
                        permissionLauncher = notificationPermissionLauncher,
                    )
                }
            }

            DisposableEffect(context, isNotificationEnabled, shouldScheduleReminderOnExit) {
                onDispose {
                    if (isNotificationEnabled && shouldScheduleReminderOnExit) {
                        PaymentReminderAlarmScheduler.schedule(context)
                    } else {
                        PaymentReminderAlarmScheduler.cancel(context)
                    }
                }
            }

            val uiState by couponViewModel.uiState.collectAsStateWithLifecycle()

            CouponScreen(
                uiState = uiState,
                isNotificationEnabled = isNotificationEnabled,
                onBackClick = { navController.popBackStack() },
                onNotificationEnabledChange = { enabled ->
                    isNotificationEnabled = enabled
                    notificationPreferenceRepository.setNotificationEnabled(enabled)
                    if (enabled) {
                        requestNotificationPermissionIfNeeded(
                            context = context,
                            permissionLauncher = notificationPermissionLauncher,
                        )
                    } else {
                        PaymentReminderAlarmScheduler.cancel(context)
                    }
                },
                onCouponSelect = couponViewModel::selectCoupon,
                onPay = {
                    submitOrder(
                        orderViewModel = orderViewModel,
                        shoppingCartViewModel = shoppingCartViewModel,
                        shoppingCartRecommendViewModel = shoppingCartRecommendViewModel,
                        orderedCartItemIds = couponViewModel.getOrderedCartItemIds(),
                        onSuccess = {
                            shouldScheduleReminderOnExit = false
                            PaymentReminderAlarmScheduler.cancel(context)
                            navigateToProductList(navController)
                        },
                    )
                },
            )
        }
    }
}

private fun requestNotificationPermissionIfNeeded(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }

    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}

private fun submitOrder(
    orderViewModel: OrderViewModel,
    shoppingCartViewModel: ShoppingCartItemViewModel,
    shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel,
    orderedCartItemIds: Set<Long>,
    onSuccess: () -> Unit = {},
) {
    if (orderedCartItemIds.isEmpty()) return

    orderViewModel.order(
        orderInfo = OrderInfo(cartItemIds = orderedCartItemIds.toList()),
        onSuccess = {
            shoppingCartViewModel.completeOrder(orderedCartItemIds) {
                shoppingCartRecommendViewModel.moveToCart()
                onSuccess()
            }
        },
    )
}

private fun resolveRecommendedCartItemIds(
    recommendedShoppingItems: List<ShoppingItem>,
    shoppingCartItems: List<ShoppingCartItem>,
): Set<Long> {
    val selectedRecommendedProductIds =
        recommendedShoppingItems
            .filter { shoppingItem -> shoppingItem.getQuantity() > 0 }
            .map { shoppingItem -> shoppingItem.getProductId() }
            .toSet()

    return shoppingCartItems
        .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedRecommendedProductIds }
        .mapTo(mutableSetOf()) { shoppingCartItem -> shoppingCartItem.getId() }
}

private const val MAX_PRODUCT_SIZE = 100
