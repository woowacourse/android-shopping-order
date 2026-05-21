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
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationEvent
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendedProductsScreen

@Composable
fun CartRecommendationRouteScreen(
    cartViewModel: CartViewModel,
    recommendationViewModel: CartRecommendationViewModel,
    onBackToCart: () -> Unit,
    onProductClick: (Long) -> Unit,
    onOrderCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(recommendationViewModel) {
        recommendationViewModel.events.collect { event ->
            when (event) {
                CartRecommendationEvent.OrderCompleted -> {
                    Toast.makeText(context, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    onOrderCompleted()
                }

                is CartRecommendationEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
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
            totalPrice = "%,d원".format(uiState.pendingOrder.totalPrice),
            selectedCount = uiState.pendingOrder.selectedCount,
            isLoading = uiState.isRecommendedProductsLoading,
            isNetworkConnected = uiState.isNetworkConnected,
            modifier = Modifier.padding(innerPadding),
            onProductClick = { product -> onProductClick(product.id) },
            onAddToCart = recommendationViewModel::addRecommendedProduct,
            onIncreaseQuantity = recommendationViewModel::increaseRecommendedProductQuantity,
            onDecreaseQuantity = recommendationViewModel::decreaseRecommendedProductQuantity,
            onOrderClick = recommendationViewModel::placeOrder,
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
