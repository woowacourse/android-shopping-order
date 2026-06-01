@file:Suppress("FunctionName")

package woowacourse.shopping.ui.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.navigation.DetailRoute

@Composable
fun DetailRouteContent(
    viewModelFactory: AppViewModelFactory,
    route: DetailRoute,
    onBack: () -> Unit,
    onNavigateToLastViewed: (Long) -> Unit,
) {
    val detailProductViewModel: DetailProductViewModel = viewModel(factory = viewModelFactory)
    val shoppingCartViewModel: ShoppingCartViewModel = viewModel(factory = viewModelFactory)

    LaunchedEffect(route.productId, route.showLastViewed) {
        detailProductViewModel.initialize(
            productId = route.productId,
            showLastViewed = route.showLastViewed,
        )
    }

    val uiState by detailProductViewModel.uiState.collectAsStateWithLifecycle()

    val shoppingItem = uiState.shoppingItem
    if (shoppingItem == null) {
        Text(text = stringResource(R.string.product_not_found_message))
        return
    }

    DetailProductScreen(
        shoppingItem = shoppingItem,
        lastViewedShoppingItem = uiState.lastViewedShoppingItem,
        onAddToCartClick = {
            shoppingCartViewModel.addOrIncreaseByProductId(
                productId = shoppingItem.getProductId(),
                amount = uiState.selectedQuantity,
                onSuccess = onBack,
            )
        },
        onLastViewedProductClick = onNavigateToLastViewed,
        onBackClick = onBack,
        quantity = uiState.selectedQuantity,
        quantityPrice = uiState.quantityPrice,
        onQuantityPlusClick = detailProductViewModel::increaseSelectedQuantity,
        onQuantityMinusClick = detailProductViewModel::decreaseSelectedQuantity,
    )
}
