@file:Suppress("FunctionName")

package woowacourse.shopping.ui.recommend

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel.ShoppingCartStep

@Composable
fun ShoppingCartRecommendRouteContent(
    viewModelFactory: AppViewModelFactory,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (Set<Long>) -> Unit,
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
    val recommendUiState by shoppingCartRecommendViewModel.uiState.collectAsStateWithLifecycle()

    val hasApiError = cartUiState.errorMessage != null
    val shoppingCartItems = cartUiState.shoppingCartItems
    val selectedProductIds = cartUiState.selectedProductIds
    val visibleItems =
        if (hasApiError) {
            emptyList()
        } else {
            shoppingCartItems
        }
    val selectableCartProductIds =
        visibleItems
            .map { shoppingCartItem -> shoppingCartItem.product.id }
            .toSet()
    val selectedVisibleProductIds = selectedProductIds.intersect(selectableCartProductIds)

    LaunchedEffect(recommendUiState.currentStep) {
        if (recommendUiState.currentStep != ShoppingCartStep.RECOMMEND) {
            onNavigateBack()
        }
    }

    BackHandler {
        shoppingCartRecommendViewModel.moveToCart()
        onNavigateBack()
    }

    ShoppingCartRecommendSection(
        recommendedShoppingItems = recommendUiState.recommendedShoppingItems,
        baseSelectedCartItemCount = recommendUiState.baseSelectedCartItemCount,
        totalPrice = recommendUiState.selectedCartTotalPrice + recommendUiState.selectedRecommendTotalPrice,
        onBackClick = {
            shoppingCartRecommendViewModel.moveToCart()
            onNavigateBack()
        },
        onOrderButtonClick = { selectedRecommendProductIds ->
            val selectedOrderProductIds = selectedVisibleProductIds + selectedRecommendProductIds
            if (selectedOrderProductIds.isEmpty()) return@ShoppingCartRecommendSection
            onNavigateToPayment(selectedOrderProductIds)
        },
        onAddToCartClick = { shoppingItem ->
            shoppingCartViewModel.addOrIncreaseByProductId(
                productId = shoppingItem.getProductId(),
                amount = 1,
            )
        },
        onQuantityPlusClick = { shoppingItem ->
            shoppingCartViewModel.addOrIncreaseByProductId(
                productId = shoppingItem.getProductId(),
                amount = 1,
            )
        },
        onQuantityMinusClick = { shoppingItem ->
            shoppingCartViewModel.decreaseByProductId(
                productId = shoppingItem.getProductId(),
            )
        },
    )
}
