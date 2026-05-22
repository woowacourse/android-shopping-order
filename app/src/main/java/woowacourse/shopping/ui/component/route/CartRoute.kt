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
import woowacourse.shopping.ui.component.screen.CartScreen
import woowacourse.shopping.ui.viewmodel.CartViewModel

@Composable
fun CartRoute(
    viewModel: CartViewModel,
    onClose: () -> Unit,
    onOrderClick: (totalPrice: Int, checkedIds: List<Long>) -> Unit,
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
        CartScreen(
            cart = uiState.items,
            currentPage = uiState.currentPage,
            onPrevious = viewModel::prev,
            onNext = viewModel::next,
            onClose = onClose,
            onAdd = { id, amount -> viewModel.updateCountWithID(id, amount) },
            onMinus = { id, amount -> viewModel.updateCountWithID(id, amount) },
            onDelete = viewModel::removeWithID,
            isPageable = uiState.isPageable,
            previousEnable = uiState.isPrevEnable,
            nextEnable = uiState.isNextEnable,
            isLoading = uiState.isLoading,
            onCheckedChanged = viewModel::onItemChecked,
            totalPrice = uiState.totalPrice,
            totalCount = uiState.checkedItemIds.size,
            isChecked = { id -> uiState.checkedItemIds.contains(id) },
            isAllChecked = viewModel.isAllChecked(),
            onSelectAllClick = viewModel::onSelectAllClick,
            onOrderClick = {
                onOrderClick(uiState.totalPrice, uiState.checkedItemIds)
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
