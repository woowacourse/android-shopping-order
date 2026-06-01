package woowacourse.shopping.ui.productDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.model.product.Product

@Composable
fun ProductDetailScreenRoute(
    productId: Int,
    appContainer: AppContainer,
    onCloseClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onLastViewedProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProductDetailViewModel = viewModel(
        factory = ProductDetailViewModel.factory(
            productId = productId,
            openedFromLastViewed = false,
            productRepository = appContainer.productRepository,
            cartRepository = appContainer.cartRepository,
            recentProductRepository = appContainer.recentProductRepository,
        ),
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProductDetailUiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                ProductDetailUiEvent.AddedToCart ->
                    onNavigateToCart()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        ProductDetailScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            onCloseClick = onCloseClick,
            onAddToCartClick = { viewModel.addToCart() },
            onLastViewedProductClick = onLastViewedProductClick,
        )
    }
}
