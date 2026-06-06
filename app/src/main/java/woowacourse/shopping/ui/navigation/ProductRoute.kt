package woowacourse.shopping.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.productDetail.ProductDetailScreen
import woowacourse.shopping.ui.productDetail.ProductDetailViewModel
import woowacourse.shopping.ui.productList.ProductListScreen
import woowacourse.shopping.ui.productList.ProductListViewModel

sealed interface ProductRoute {
    @Serializable
    data object ProductList : ProductRoute

    @Serializable
    data class Detail(
        val productId: Int,
        val openedFromLastViewed: Boolean,
    ) : ProductRoute
}

fun NavGraphBuilder.productNavGraph(
    navController: NavController,
    onNavigateToCart: () -> Unit,
    onNavigateToSetting: () -> Unit,
) {
    composable<ProductRoute.ProductList> {
        val viewModel: ProductListViewModel = viewModel(factory = ProductListViewModel.Factory)
        ProductListScreen(
            viewModel = viewModel,
            onCartClick = onNavigateToCart,
            onSettingClick = onNavigateToSetting,
            onProductClick = { productId ->
                navController.navigate(
                    ProductRoute.Detail(
                        productId,
                        openedFromLastViewed = false,
                    ),
                )
            },
        )
    }

    composable<ProductRoute.Detail> {
        val viewModel: ProductDetailViewModel =
            viewModel(factory = ProductDetailViewModel.Factory)
        ProductDetailScreen(
            viewModel = viewModel,
            onCloseClick = { navController.popBackStack() },
            onLastViewedProductClick = { product ->
                navController.navigate(
                    ProductRoute.Detail(
                        product.id,
                        openedFromLastViewed = true,
                    ),
                )
            },
        )
    }
}
