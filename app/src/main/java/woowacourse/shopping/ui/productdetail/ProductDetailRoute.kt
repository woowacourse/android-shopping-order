package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute
import woowacourse.shopping.ui.uimodel.toProductUiModel

fun NavGraphBuilder.productDetailRoute(
    cartRepository: CartRepository,
    productRepository: ProductRepository,
    recentlyViewedProductRepository: RecentlyViewedProductRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onLastViewedProductClick: (selectedProductId: Long, lastViewedProductId: Long) -> Unit,
    onBackClick: () -> Unit,
) {
    composable<ShoppingRoute.ProductDetail> {
        ProductDetailRouteContent(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentlyViewedProductRepository = recentlyViewedProductRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onLastViewedProductClick = onLastViewedProductClick,
            onBackClick = onBackClick,
        )
    }
}

@Composable
private fun ProductDetailRouteContent(
    cartRepository: CartRepository,
    productRepository: ProductRepository,
    recentlyViewedProductRepository: RecentlyViewedProductRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onLastViewedProductClick: (selectedProductId: Long, lastViewedProductId: Long) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: ProductDetailViewModel =
        viewModel(
            factory =
                ProductDetailViewModelFactory(
                    cartRepository = cartRepository,
                    recentlyViewedProductRepository = recentlyViewedProductRepository,
                    productRepository = productRepository,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val count by viewModel.countState.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val lastViewedProduct by viewModel.lastViewedProduct.collectAsStateWithLifecycle()

    selectedProduct?.let { product ->
        val uiState =
            ProductDetailUiState(
                product = product.toProductUiModel(),
                count = count,
                lastViewedProduct = lastViewedProduct?.toProductUiModel(),
            )

        ProductDetailScreen(
            uiState = uiState,
            onLastViewedClick = { clickedProductId ->
                lastViewedProduct?.let { viewModel.updateHistory(it) }
                onLastViewedProductClick(clickedProductId, product.id)
            },
            onAdd = { viewModel.addCount() },
            onMinus = { viewModel.minusCount() },
            onAddRequest = {
                viewModel.addPurchaseProduct(
                    purchaseProduct =
                        PurchaseProduct(
                            product.id,
                            product,
                            count,
                        ),
                    onSuccess = onBackClick,
                )
            },
            onClose = onBackClick,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
