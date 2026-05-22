package woowacourse.shopping.ui.navigation

import Cart
import CartRecommendation
import ProductDetail
import ProductList
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.cart.list.CartScreen
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.list.CartViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendedProductsScreen
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.productdetail.ProductDetailViewModel
import woowacourse.shopping.ui.productdetail.ProductDetailViewModelFactory
import woowacourse.shopping.ui.shopping.ShoppingScreen
import woowacourse.shopping.ui.shopping.ShoppingViewModel
import woowacourse.shopping.ui.shopping.ShoppingViewModelFactory

@Composable
fun AppNavHost(innerPadding: PaddingValues) {
    val navController = rememberNavController()

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
                onMoreClick = viewModel::loadMore,
                onAddToCart = viewModel::addToCart,
                onIncreaseQuantity = viewModel::increaseQuantity,
                onDecreaseQuantity = viewModel::decreaseQuantity,
            )
        }

        composable<ProductDetail> {
            val viewModel: ProductDetailViewModel =
                viewModel(
                    factory = ProductDetailViewModelFactory(),
                )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

        composable<Cart> {
            val viewModel: CartViewModel =
                viewModel(
                    factory = CartViewModelFactory(),
                )
            val coroutineScope = rememberCoroutineScope()

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CartScreen(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onBackClick = { navController.popBackStack() },
                onOrderClick = {
                    coroutineScope.launch {
                        val selectedIds = viewModel.getSelectedCartItemIds()
                        if (selectedIds.isNotEmpty()) {
                            navController.navigate(CartRecommendation(selectedIds.toLongArray()))
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

        composable<CartRecommendation> {
            val cartViewModel: CartViewModel =
                viewModel(
                    factory = CartViewModelFactory(),
                )
            val recommendationViewModel: CartRecommendationViewModel =
                viewModel(
                    factory = CartRecommendationViewModelFactory(),
                )

            val uiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()

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
                onOrderClick = recommendationViewModel::placeOrder,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
