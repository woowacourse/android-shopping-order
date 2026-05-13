package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.theme.ShoppingTheme

class CartActivity : ComponentActivity() {
    private val viewModel: CartViewModel by viewModels()

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, CartActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadVisibleState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            var isShowingRecommendedProducts by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(uiState.orderCompletedCount) {
                if (uiState.orderCompletedCount > 0) {
                    Toast.makeText(this@CartActivity, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            LaunchedEffect(uiState.orderErrorMessage) {
                val message = uiState.orderErrorMessage ?: return@LaunchedEffect
                Toast.makeText(this@CartActivity, message, Toast.LENGTH_SHORT).show()
                viewModel.clearOrderError()
            }

            BackHandler(enabled = isShowingRecommendedProducts) {
                isShowingRecommendedProducts = false
            }

            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isShowingRecommendedProducts) {
                        CartRecommendedProductsScreen(
                            recommendedProducts = uiState.recommendedProducts,
                            totalPrice = formatPrice(uiState.pendingOrder.totalPrice),
                            selectedCount = uiState.pendingOrder.selectedCount,
                            isLoading = uiState.isRecommendedProductsLoading,
                            isNetworkConnected = uiState.isNetworkConnected,
                            modifier = Modifier.padding(innerPadding),
                            onProductClick = { product ->
                                ProductDetailActivity.startActivity(this, product.id)
                            },
                            onAddToCart = viewModel::increaseQuantity,
                            onIncreaseQuantity = viewModel::increaseQuantity,
                            onDecreaseQuantity = viewModel::decreaseQuantity,
                            onOrderClick = viewModel::placeOrder,
                            onBackClick = { isShowingRecommendedProducts = false },
                        )
                    } else {
                        CartScreen(
                            cartListState = uiState.cartListState,
                            isNetworkConnected = uiState.isNetworkConnected,
                            modifier = Modifier.padding(innerPadding),
                            onBackClick = ::finish,
                            onOrderClick = {
                                if (viewModel.prepareOrder()) {
                                    isShowingRecommendedProducts = true
                                }
                            },
                            onItemCheckedChange = viewModel::toggleItemSelection,
                            onDeleteClick = viewModel::delete,
                            onIncreaseQuantity = viewModel::increaseQuantity,
                            onDecreaseQuantity = viewModel::decreaseQuantity,
                            onPreviousClick = viewModel::loadPreviousPage,
                            onNextClick = viewModel::loadNextPage,
                        )
                    }
                }
            }
        }
    }
}

private fun formatPrice(totalPrice: Int): String = "%,d원".format(totalPrice)
