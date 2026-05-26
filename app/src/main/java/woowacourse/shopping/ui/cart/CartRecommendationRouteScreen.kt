package woowacourse.shopping.ui.cart

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationEvent
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendedProductsScreen
import woowacourse.shopping.ui.common.formatter.formatPrice

@Composable
fun CartRecommendationRouteScreen(
    cartViewModel: CartViewModel,
    recommendationViewModel: CartRecommendationViewModel,
    onBackToCart: () -> Unit,
    onProductClick: (Long) -> Unit,
    onProceedToOrder: (SelectedCartOrder) -> Unit,
) {
    val context = LocalContext.current
    val uiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(recommendationViewModel) {
        recommendationViewModel.events.collect { event ->
            when (event) {
                is CartRecommendationEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                CartRecommendationEvent.OrderCompleted -> Unit
            }
        }
    }

    BackHandler(enabled = !uiState.isReturningToCart) {
        if (!recommendationViewModel.beginReturningToCart()) return@BackHandler
        scope.launch {
            runCatching {
                recommendationViewModel.awaitPendingChanges()
                cartViewModel.reloadVisibleStateImmediately()
            }.onSuccess {
                onBackToCart()
            }.onFailure {
                recommendationViewModel.resetReturningToCart()
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        CartRecommendedProductsScreen(
            recommendedProducts = uiState.recommendedProducts,
            totalPrice = formatPrice(uiState.pendingOrder.totalPrice),
            selectedCount = uiState.pendingOrder.selectedCount,
            isLoading = uiState.isRecommendedProductsLoading,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onProductClick = { product -> onProductClick(product.id) },
            onAddToCart = recommendationViewModel::addRecommendedProduct,
            onIncreaseQuantity = recommendationViewModel::increaseRecommendedProductQuantity,
            onDecreaseQuantity = recommendationViewModel::decreaseRecommendedProductQuantity,
            onOrderClick = {
                scope.launch {
                    recommendationViewModel.awaitPendingChanges()
                    cartViewModel.reloadVisibleStateImmediately()
                    val selectedCartOrder = recommendationViewModel.createSelectedCartOrder() ?: return@launch
                    onProceedToOrder(selectedCartOrder)
                }
            },
            onBackClick = {
                if (!recommendationViewModel.beginReturningToCart()) return@CartRecommendedProductsScreen
                scope.launch {
                    runCatching {
                        recommendationViewModel.awaitPendingChanges()
                        cartViewModel.reloadVisibleStateImmediately()
                    }.onSuccess {
                        onBackToCart()
                    }.onFailure {
                        recommendationViewModel.resetReturningToCart()
                    }
                }
            },
        )
    }
}
