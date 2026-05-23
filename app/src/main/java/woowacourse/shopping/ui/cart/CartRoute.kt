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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.navigation.Recommendation

@Composable
fun CartRoute(
    viewModel: CartViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbatJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is CartEvent.SnackbarEvent -> {
                        snackbatJob?.cancel()
                        snackbatJob = launch {
                            snackbarHostState.showSnackbar(
                                event.errorMsg
                            )
                        }
                    }

                    is CartEvent.UpdateCount ->
                        viewModel.updateCountWithID(
                            id = event.targetId,
                            updateAmount = event.updateAmount
                        )

                    is CartEvent.RemoveFromCart ->
                        viewModel.removeWithID(event.targetId)

                    is CartEvent.NextPage -> viewModel.next()

                    is CartEvent.PrevPage -> viewModel.prev()

                    is CartEvent.NavigateToShopping -> navController.popBackStack()

                    is CartEvent.NavigateToRecommendation -> navController.navigate(
                        Recommendation(
                            totalPrice = event.totalPrice,
                            checkedIds = event.checkedIds
                        )
                    )
                }
            }
        }
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
                viewModel.navigateToRecommendation(
                    totalPrice = uiState.totalPrice,
                    checkedIds = uiState.checkedItemIds
                )
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
