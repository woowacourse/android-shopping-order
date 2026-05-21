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
import woowacourse.shopping.ui.component.screen.CartRecommendationScreen
import woowacourse.shopping.ui.viewmodel.RecommendationViewModel

@Composable
fun RecommendationRoute(
    viewModel: RecommendationViewModel,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
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
        CartRecommendationScreen(
            recommendedProducts = uiState.recommendedProducts,
            totalPrice = uiState.totalPrice,
            totalCount = uiState.checkedIds.size,
            onBackClick = onBackClick,
            onOrderClick = onOrderClick,
            onAddInCart = viewModel::addToCart,
            onAdd = viewModel::updateCountWithID,
            onMinus = viewModel::updateCountWithID,
            onDelete = viewModel::removeWithID,
            onItemClick = { product ->
                viewModel.updateHistory(product)
                onNavigateToProductDetail(product.id)
            },
            isContainedInCart = { id -> uiState.cart.isContain(id) },
            itemCount = { id -> uiState.cart.totalCountOfSpecificPurchaseProduct(id) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
