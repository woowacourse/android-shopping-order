package woowacourse.shopping.ui.component.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.component.screen.ProductDetailScreen
import woowacourse.shopping.ui.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel,
    onClose: () -> Unit,
    onNavigateToProductDetail: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorMsgShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        uiState.product?.let { product ->
            ProductDetailScreen(
                product = product,
                count = uiState.count,
                lastViewedProduct = uiState.lastViewProduct,
                onLastViewedClick = { lastViewed ->
                    viewModel.updateHistory(lastViewed)
                    onNavigateToProductDetail(lastViewed.id)
                },
                onAdd = viewModel::addCount,
                onMinus = viewModel::minusCount,
                onAddRequest = {
                    viewModel.addPurchaseProduct(
                        PurchaseProduct(
                            product = product,
                            id = product.id,
                            count = uiState.count,
                        ),
                    )
                    onClose()
                },
                onClose = onClose,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
