package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.productDetailRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    onLastViewedProductClick: (selectedProductId: Long, lastViewedProductId: Long) -> Unit,
    onBackClick: () -> Unit,
) {
    composable<ShoppingRoute.ProductDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<ShoppingRoute.ProductDetail>()

        ProductDetailRouteContent(
            shoppingApplication = shoppingApplication,
            selectedProductId = route.selectedProductId,
            lastViewedProductId = route.lastViewedProductId,
            contentPadding = contentPadding,
            onLastViewedProductClick = onLastViewedProductClick,
            onBackClick = onBackClick,
        )
    }
}

@Composable
private fun ProductDetailRouteContent(
    shoppingApplication: ShoppingApplication,
    selectedProductId: Long,
    lastViewedProductId: Long?,
    contentPadding: PaddingValues,
    onLastViewedProductClick: (selectedProductId: Long, lastViewedProductId: Long) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: ProductDetailViewModel =
        viewModel(
            factory =
                ProductDetailViewModelFactory(
                    cartRepository = shoppingApplication.cartRepository,
                    recentlyViewedProductRepository =
                        shoppingApplication.recentlyViewedProductRepository,
                    productRepository = shoppingApplication.productRepository,
                    selectedProductId = selectedProductId,
                    lastViewedProductId = lastViewedProductId,
                ),
        )
    val count by viewModel.countState.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val lastViewedProduct by viewModel.lastViewedProduct.collectAsStateWithLifecycle()

    selectedProduct?.let { product ->
        ProductDetailScreen(
            product = product,
            count = count,
            lastViewedProduct = lastViewedProduct,
            onLastViewedClick = { clickedProduct ->
                viewModel.updateHistory(clickedProduct)
                onLastViewedProductClick(clickedProduct.id, product.id)
            },
            onAdd = { viewModel.addCount() },
            onMinus = { viewModel.minusCount() },
            onAddRequest = {
                viewModel.addPurchaseProduct(
                    PurchaseProduct(
                        product.id,
                        product,
                        count,
                    ),
                )
                onBackClick()
            },
            onClose = onBackClick,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
