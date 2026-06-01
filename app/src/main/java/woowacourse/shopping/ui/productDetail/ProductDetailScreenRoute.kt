package woowacourse.shopping.ui.productDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.model.product.Product

@Composable
fun ProductDetailScreenRoute(
    productId: Int,
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
    onCloseClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onLastViewedProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProductDetailViewModel =
        viewModel(
            factory =
                ProductDetailViewModel.factory(
                    productId = productId,
                    openedFromLastViewed = false,
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentProductRepository = appContainer.recentProductRepository,
                ),
        )

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProductDetailUiEvent.ShowSnackbar -> showSnackbar(event.message)
                ProductDetailUiEvent.AddedToCart -> onNavigateToCart()
            }
        }
    }

    ProductDetailScreen(
        modifier = modifier,
        viewModel = viewModel,
        onCloseClick = onCloseClick,
        onAddToCartClick = { viewModel.addToCart() },
        onLastViewedProductClick = onLastViewedProductClick,
    )
}
