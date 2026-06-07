package woowacourse.shopping.ui.productList

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.ui.util.ObserveAsEvents

@Composable
fun ProductListScreenRoute(
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProductListViewModel =
        viewModel(
            factory =
                ProductListViewModel.factory(
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository,
                    recentProductRepository = appContainer.recentProductRepository,
                ),
        )

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is ProductListUiEvent.ShowSnackbar -> showSnackbar(event.message)
        }
    }

    ProductListScreen(
        modifier = modifier,
        viewModel = viewModel,
        onCartClick = onCartClick,
        onSettingsClick = onSettingsClick,
        onProductClick = onProductClick,
    )
}
