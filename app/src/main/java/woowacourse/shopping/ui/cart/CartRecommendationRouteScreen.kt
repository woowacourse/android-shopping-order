package woowacourse.shopping.ui.cart

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.cart.list.CartViewModel
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
    var isReturningToCart by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.orderCompletedCount) {
        if (uiState.orderCompletedCount > 0) {
            Toast.makeText(context, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            onOrderCompleted()
        }
    }

    LaunchedEffect(uiState.orderErrorMessage) {
        val message = uiState.orderErrorMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        recommendationViewModel.clearOrderError()
    }

    BackHandler(enabled = !isReturningToCart) {
        isReturningToCart = true
        scope.launch {
            recommendationViewModel.awaitPendingChanges()
            cartViewModel.reloadVisibleStateImmediately()
            onBackToCart()
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
                if (isReturningToCart) return@CartRecommendedProductsScreen
                isReturningToCart = true
                scope.launch {
                    recommendationViewModel.awaitPendingChanges()
                    cartViewModel.reloadVisibleStateImmediately()
                    onBackToCart()
                }
            },
        )
    }
}
