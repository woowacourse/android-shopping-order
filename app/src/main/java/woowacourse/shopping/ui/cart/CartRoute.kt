package woowacourse.shopping.ui.cart

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CartRoute(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onNavigateToRecommendation: (Int, List<Long>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbarJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is CartEvent.SnackbarEvent -> {
                        val message = event.errorMsg.toDisplayString(context)
                        snackbarJob?.cancel()
                        snackbarJob =
                            launch {
                                snackbarHostState.showSnackbar(
                                    message,
                                )
                            }
                    }

                    is CartEvent.UpdateCount ->
                        viewModel.updateCountWithID(
                            id = event.targetId,
                            updateAmount = event.updateAmount,
                        )

                    is CartEvent.RemoveFromCart ->
                        viewModel.removeWithID(event.targetId)

                    is CartEvent.NextPage -> viewModel.next()

                    is CartEvent.PrevPage -> viewModel.prev()

                    is CartEvent.NavigateToShopping -> onBack()

                    is CartEvent.NavigateToRecommendation ->
                        onNavigateToRecommendation(
                            event.totalPrice,
                            event.checkedIds,
                        )
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.fetchCart()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        CartScreen(
            cart = uiState.items,
            currentPage = uiState.currentPage,
            onPrevious = viewModel::prevPageTrigger,
            onNext = viewModel::nextPageTrigger,
            onClose = viewModel::navigateToShopping,
            onAdd = { id, amount -> viewModel.updateAmountTrigger(id, amount) },
            onMinus = { id, amount -> viewModel.updateAmountTrigger(id, amount) },
            onDelete = viewModel::removeItemTrigger,
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
                viewModel.navigateToRecommendation(
                    totalPrice = uiState.totalPrice,
                    checkedIds = uiState.checkedItemIds,
                )
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
