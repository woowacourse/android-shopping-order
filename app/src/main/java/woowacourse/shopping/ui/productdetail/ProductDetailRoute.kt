package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.PurchaseProduct

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel,
    onNavigateToLastViewed: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbarJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is ProductDetailEvent.SnackbarEvent -> {
                        snackbarJob?.cancel()
                        snackbarJob =
                            launch {
                                snackbarHostState.showSnackbar(
                                    event.errorMsg,
                                )
                            }
                    }
                    is ProductDetailEvent.MoveToLastViewedProductDetail ->
                        onNavigateToLastViewed(event.lastViewedProductId)
                    is ProductDetailEvent.MoveToShopping ->
                        onBack()
                }
            }
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
                    viewModel.moveToLastViewedProduct(lastViewed.id)
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
                    viewModel.moveToShopping()
                },
                onClose = viewModel::moveToShopping,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
