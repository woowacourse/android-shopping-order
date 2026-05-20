package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.cart.list.CartScreen
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.list.CartViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModelFactory
import woowacourse.shopping.ui.cart.recommendation.CartRecommendedProductsScreen
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.theme.ShoppingTheme

class CartActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory()
    }
    private val recommendationViewModel: CartRecommendationViewModel by viewModels {
        CartRecommendationViewModelFactory()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.reloadVisibleState()
        recommendationViewModel.reloadVisibleState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()
            val recommendationUiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()
            var isShowingRecommendedProducts by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(recommendationUiState.orderCompletedCount) {
                if (recommendationUiState.orderCompletedCount > 0) {
                    Toast.makeText(this@CartActivity, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            LaunchedEffect(recommendationUiState.orderErrorMessage) {
                val message = recommendationUiState.orderErrorMessage ?: return@LaunchedEffect
                Toast.makeText(this@CartActivity, message, Toast.LENGTH_SHORT).show()
                recommendationViewModel.clearOrderError()
            }

            BackHandler(enabled = isShowingRecommendedProducts) {
                isShowingRecommendedProducts = false
            }

            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isShowingRecommendedProducts) {
                        CartRecommendedProductsScreen(
                            recommendedProducts = recommendationUiState.recommendedProducts,
                            totalPrice = formatPrice(recommendationUiState.pendingOrder.totalPrice),
                            selectedCount = recommendationUiState.pendingOrder.selectedCount,
                            isLoading = recommendationUiState.isRecommendedProductsLoading,
                            isNetworkConnected = recommendationUiState.isNetworkConnected,
                            modifier = Modifier.padding(innerPadding),
                            onProductClick = { product ->
                                ProductDetailActivity.startActivity(this, product.id)
                            },
                            onAddToCart = recommendationViewModel::addRecommendedProduct,
                            onIncreaseQuantity = recommendationViewModel::addRecommendedProduct,
                            onDecreaseQuantity = recommendationViewModel::decreaseRecommendedProductQuantity,
                            onOrderClick = recommendationViewModel::placeOrder,
                            onBackClick = { isShowingRecommendedProducts = false },
                        )
                    } else {
                        CartScreen(
                            cartUiState = cartUiState,
                            modifier = Modifier.padding(innerPadding),
                            onBackClick = ::finish,
                            onOrderClick = {
                                lifecycleScope.launch {
                                    val selectedCartOrder = cartViewModel.createSelectedCartOrder()
                                    if (selectedCartOrder != null) {
                                        recommendationViewModel.startOrder(selectedCartOrder)
                                        isShowingRecommendedProducts = true
                                    }
                                }
                            },
                            onItemCheckedChange = cartViewModel::toggleItemSelection,
                            onAllCheckedChange = cartViewModel::toggleAllSelection,
                            onDeleteClick = cartViewModel::delete,
                            onIncreaseQuantity = cartViewModel::increaseQuantity,
                            onDecreaseQuantity = cartViewModel::decreaseQuantity,
                            onPreviousClick = cartViewModel::loadPreviousPage,
                            onNextClick = cartViewModel::loadNextPage,
                        )
                    }
                }
            }
        }
    }
}

private fun formatPrice(totalPrice: Int): String = "%,d원".format(totalPrice)
