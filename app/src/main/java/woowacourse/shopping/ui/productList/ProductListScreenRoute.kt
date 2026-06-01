package woowacourse.shopping.ui.productList

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
fun ProductListScreenRoute(
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProductListViewModel = viewModel(
        factory = ProductListViewModel.factory(
            productRepository = appContainer.productRepository,
            cartRepository = appContainer.cartRepository,
            recentProductRepository = appContainer.recentProductRepository,
        ),
    )

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProductListUiEvent.ShowSnackbar -> showSnackbar(event.message)
            }
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
