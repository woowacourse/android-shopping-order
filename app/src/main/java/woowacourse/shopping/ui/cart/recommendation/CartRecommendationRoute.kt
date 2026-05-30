package woowacourse.shopping.ui.cart.recommendation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.list.CartViewModelFactory
import woowacourse.shopping.ui.navigation.OrderProduct

@Composable
fun CartRecommendationRoute(
    cartFlowEntry: NavBackStackEntry,
    onProductClick: (Long) -> Unit,
    onOrderProductsReady: (List<OrderProduct>) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cartViewModel: CartViewModel =
        viewModel(
            viewModelStoreOwner = cartFlowEntry,
            factory = CartViewModelFactory(),
        )
    val recommendationViewModel: CartRecommendationViewModel =
        viewModel(
            factory = CartRecommendationViewModelFactory(),
        )

    val uiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(cartViewModel.orderProductsEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            cartViewModel.orderProductsEvent.collect { orderProducts ->
                onOrderProductsReady(orderProducts)
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        cartViewModel.reloadVisibleState()
        recommendationViewModel.reloadVisibleState()
    }

    CartRecommendedProductsScreen(
        uiState = uiState,
        modifier = modifier,
        onProductClick = onProductClick,
        onAddToCart = recommendationViewModel::addRecommendedProduct,
        onIncreaseQuantity = recommendationViewModel::addRecommendedProduct,
        onDecreaseQuantity = recommendationViewModel::decreaseRecommendedProductQuantity,
        onOrderClick = {
            coroutineScope.launch {
                if (recommendationViewModel.applyRecommendations()) {
                    cartViewModel.orderSelectedProducts()
                }
            }
        },
        onBackClick = onBackClick,
    )
}
