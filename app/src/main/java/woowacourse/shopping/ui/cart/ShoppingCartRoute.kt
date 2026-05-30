@file:Suppress("FunctionName")

package woowacourse.shopping.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel

@Composable
fun ShoppingCartRouteContent(
    viewModelFactory: AppViewModelFactory,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    onNavigateBack: () -> Unit,
    onNavigateToRecommend: () -> Unit,
) {
    val shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )
    val shoppingCartViewModel: ShoppingCartViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )

    val cartUiState by shoppingCartViewModel.uiState.collectAsStateWithLifecycle()
    val cartScreenState by shoppingCartViewModel.screenState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        shoppingCartViewModel.requestCartItems()
        onPauseOrDispose { }
    }

    val hasApiError = cartScreenState is ShoppingCartViewModel.ShoppingCartScreenState.Error
    val isLoading = cartScreenState is ShoppingCartViewModel.ShoppingCartScreenState.Loading
    val shoppingCartItems = cartUiState.shoppingCartItems
    val selectedProductIds = cartUiState.selectedProductIds
    val visibleItems =
        if (hasApiError) {
            emptyList()
        } else {
            shoppingCartItems
        }
    val visiblePagedItems =
        if (hasApiError) {
            emptyList()
        } else {
            cartUiState.pagedItems
        }
    val selectableCartProductIds =
        visibleItems
            .map { shoppingCartItem -> shoppingCartItem.product.id }
            .toSet()
    val selectedVisibleProductIds = selectedProductIds.intersect(selectableCartProductIds)
    val selectedItemCount = selectedVisibleProductIds.size
    val selectedCartTotalPrice =
        shoppingCartItems
            .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedVisibleProductIds }
            .sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }

    LaunchedEffect(shoppingCartItems, selectedProductIds) {
        shoppingCartRecommendViewModel.updateCartSnapshot(
            shoppingCartItems = shoppingCartItems,
            selectedCartProductIds = selectedVisibleProductIds,
        )
    }

    ShoppingCartScreen(
        shoppingCartItems = visiblePagedItems,
        selectedProductIds = selectedVisibleProductIds,
        isLoading = isLoading,
        getQuantityPrice = shoppingCartViewModel::getQuantityPrice,
        onBackClick = onNavigateBack,
        onRemoveShoppingItemClick = { shoppingCartItem ->
            shoppingCartViewModel.removeShoppingItem(shoppingCartItem)
        },
        onToggleShoppingItemSelectionClick = { productId, isSelected ->
            shoppingCartViewModel.setShoppingCartProductSelection(
                productId = productId,
                isSelected = isSelected,
            )
        },
        onIncreaseShoppingItemQuantityClick = { shoppingCartItem ->
            shoppingCartViewModel.increaseShoppingItemQuantity(shoppingCartItem)
        },
        onDecreaseShoppingItemQuantityClick = { shoppingCartItem ->
            shoppingCartViewModel.decreaseShoppingItemQuantity(shoppingCartItem)
        },
    ) {
        PageNavigation(
            currentPage = cartUiState.currentPage,
            canMoveToPreviousPage = if (hasApiError) false else cartUiState.canMoveToPreviousPage,
            canMoveToNextPage = if (hasApiError) false else cartUiState.canMoveToNextPage,
            onBeforePageClick = shoppingCartViewModel::moveToPreviousPage,
            onNextPageClick = shoppingCartViewModel::moveToNextPage,
        )
        OrderButton(
            shoppingCartItems = visibleItems,
            selectedProductIds = selectedVisibleProductIds,
            shoppingCartSelectItemCount = selectedItemCount,
            onOrderButtonClick = { selectedIds ->
                if (selectedIds.isEmpty()) return@OrderButton
                shoppingCartRecommendViewModel.moveToRecommend(
                    baseCartItems = shoppingCartItems,
                    baseSelectedCartProductIds = selectedVisibleProductIds,
                )
                onNavigateToRecommend()
            },
            checked =
                selectableCartProductIds.isNotEmpty() &&
                    selectedVisibleProductIds.size == selectableCartProductIds.size,
            orderComplete = visibleItems.isNotEmpty(),
            totalPrice = selectedCartTotalPrice,
            onToggleShoppingItemSelectionClick = { productIds, isSelected ->
                shoppingCartViewModel.setShoppingCartProductsSelection(
                    productIds = productIds,
                    isSelected = isSelected,
                )
            },
        )
    }
}
