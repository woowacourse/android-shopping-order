package woowacourse.shopping.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.CartRoute
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.CartViewModelFactory
import woowacourse.shopping.ui.navigation.Cart
import woowacourse.shopping.ui.navigation.ProductDetail
import woowacourse.shopping.ui.navigation.Recommendation
import woowacourse.shopping.ui.navigation.Shopping
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
                            val viewModel: ShoppingViewModel = viewModel(
                                factory = ShoppingViewModelFactory(
                                    cartRepository = app.cartRepository,
                                    recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                    productRepository = app.productRepository
                                )
                            )
                            ShoppingRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable<ProductDetail> { backStackEntry ->
                            val route: ProductDetail = backStackEntry.toRoute()

                            val viewModel: ProductDetailViewModel = viewModel(
                                factory = ProductDetailViewModelFactory(
                                    cartRepository = app.cartRepository,
                                    recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                    productRepository = app.productRepository,
                                    selectedProductId = route.selectedProductId,
                                    lastViewedProductId = route.lastViewedProductId
                                )
                            )
                            ProductDetailRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }

                        composable<Cart> {
                            val viewModel: CartViewModel = viewModel(
                                factory = CartViewModelFactory(
                                    cartRepository = app.cartRepository
                                )
                            )
                            CartRoute(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable<Recommendation> { backStackEntry ->
                            val route: Recommendation = backStackEntry.toRoute()

                            val viewModel: RecommendationViewModel = viewModel(
                                factory = RecommendationViewModelFactory(
                                    cartRepository = app.cartRepository,
                                    productRepository = app.productRepository,
                                    recentlyViewedProductRepository = app.recentlyViewedProductRepository,
                                    initPrice = route.totalPrice,
                                    initCheckItemIds = route.checkedIds
                                )
                            )
                            RecommendationRoute(
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onOrderClick = {
                                    navController.popBackStack(Shopping, inclusive = false)
                                },
                                onNavigateToProductDetail = { id ->
                                    navController.navigate(ProductDetail(id))
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}