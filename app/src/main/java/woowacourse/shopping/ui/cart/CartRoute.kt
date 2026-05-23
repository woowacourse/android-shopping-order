package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.ui.cart.uimodel.toUiModel
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.cartRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onOrderClick: (selectedCartItemIds: List<Long>) -> Unit,
) {
    composable<ShoppingRoute.Cart> {
        cartContent(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            onBackClick = onBackClick,
            onOrderClick = onOrderClick,
        )
    }
}

@Composable
private fun cartContent(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
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
    val pagedCart by viewModel.pagedCart.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val isPageable by viewModel.isPageable.collectAsStateWithLifecycle()
    val nextEnable by viewModel.nextEnable.collectAsStateWithLifecycle()
    val prevEnable by viewModel.prevEnable.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val checkedItemIds by viewModel.checkedItemIds.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val totalCount by viewModel.selectedItemCount.collectAsStateWithLifecycle()
    val uiState =
        CartUiState(
            cartItems = pagedCart.toUiModel(),
            currentPage = currentPage,
            isPageable = isPageable,
            previousEnable = prevEnable,
            nextEnable = nextEnable,
            isLoading = isLoading,
            totalPrice = totalPrice,
            totalCount = totalCount,
            checkedItemIds = checkedItemIds,
        )
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
            onOrderClick(checkedItemIds)
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding),
    )
}
