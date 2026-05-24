package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.recommend.RecommendScreen
import woowacourse.shopping.ui.cart.recommend.RecommendViewModel
import woowacourse.shopping.ui.productDetail.ProductDetailScreen
import woowacourse.shopping.ui.productDetail.ProductDetailViewModel
import woowacourse.shopping.ui.productList.ProductListScreen
import woowacourse.shopping.ui.productList.ProductListViewModel

@Composable
fun ShoppingNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProductList,
    ) {
        composable<ProductList> {
            val viewModel: ProductListViewModel = viewModel(factory = ProductListViewModel.Factory)
            ProductListScreen(
                viewModel = viewModel,
                onCartClick = { navController.navigate(CartGraph) },
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId, openedFromLastViewed = false))
                },
            )
        }

        composable<ProductDetail> {
            val viewModel: ProductDetailViewModel =
                viewModel(factory = ProductDetailViewModel.Factory)
            ProductDetailScreen(
                viewModel = viewModel,
                onCloseClick = { navController.popBackStack() },
                onAddToCartClick = { viewModel.addToCart() },
                onLastViewedProductClick = { product ->
                    navController.navigate(ProductDetail(product.id, openedFromLastViewed = true))
                },
            )
        }

        navigation<CartGraph>(startDestination = Cart) {
            composable<Cart> { entry ->
                val cartGraphEntry =
                    remember(entry) {
                        navController.getBackStackEntry<CartGraph>()
                    }
                val cartViewModel: CartViewModel =
                    viewModel(
                        viewModelStoreOwner = cartGraphEntry,
                        factory = CartViewModel.Factory,
                    )
                CartScreen(
                    viewModel = cartViewModel,
                    onClickClose = { navController.popBackStack() },
                    onNavigateToRecommend = { navController.navigate(Recommend) },
                )
            }

            composable<Recommend> { entry ->
                val cartGraphEntry =
                    remember(entry) {
                        navController.getBackStackEntry<CartGraph>()
                    }
                val cartViewModel: CartViewModel =
                    viewModel(
                        viewModelStoreOwner = cartGraphEntry,
                        factory = CartViewModel.Factory,
                    )
                val recommendViewModel: RecommendViewModel =
                    viewModel(factory = RecommendViewModel.Factory)
                RecommendScreen(
                    cartViewModel = cartViewModel,
                    recommendViewModel = recommendViewModel,
                    onClickClose = { navController.popBackStack() },
                    onOrderSuccess = {
                        navController.navigate(ProductList) {
                            popUpTo<ProductList> {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        }
    }
}
