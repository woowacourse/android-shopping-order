package woowacourse.shopping.ui.cart

import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.cartRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onOrderClick: (selectedCartItemIds: List<Long>) -> Unit,
) {
    composable<ShoppingRoute.Cart> {
        cartContent(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onOrderClick = onOrderClick,
        )
    }
}

@Composable
private fun cartContent(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onOrderClick: (selectedCartItemIds: List<Long>) -> Unit,
) {
    val viewModel: CartViewModel =
        viewModel(
            factory =
                CartViewModelFactory(
                    cartRepository = shoppingApplication.cartRepository,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CartScreen(
        uiState = uiState,
        onPrevious = { viewModel.prev() },
        onNext = { viewModel.next() },
        onClose = onBackClick,
        onAdd = { id, updateAmount ->
            viewModel.updateCountWithID(id, updateAmount)
        },
        onMinus = { id, updateAmount ->
            viewModel.updateCountWithID(id, updateAmount)
        },
        onDelete = { id ->
            viewModel.removeWithID(id)
        },
        onCheckedChanged = { viewModel.onItemChecked(it) },
        onSelectAllClick = { viewModel.onSelectAllClick() },
        onOrderClick = {
            onOrderClick(uiState.checkedItemIds)
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding),
    )
}
