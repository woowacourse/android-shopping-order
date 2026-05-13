package woowacourse.shopping.ui.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.theme.ShoppingTheme
class ProductDetailActivity : ComponentActivity() {
    private val viewModel: ProductDetailViewModel by viewModels()

    companion object {
        private const val PUT_EXTRA_KEY_PRODUCT_ID = "PRODUCT_ID"

        fun startActivity(
            context: Context,
            productId: Long,
        ) {
            val intent =
                Intent(context, ProductDetailActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(PUT_EXTRA_KEY_PRODUCT_ID, productId)
                }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedProductId = parse(intent)
        if (receivedProductId == null) {
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(receivedProductId) {
                viewModel.loadProduct(receivedProductId)
            }

            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val product = uiState.product ?: return@Scaffold

                    woowacourse.shopping.ui.productdetail.ProductDetailScreen(
                        product = product,
                        lastViewedProduct = uiState.lastViewedProduct,
                        quantity = uiState.quantity,
                        isAdding = uiState.isAdding,
                        isNetworkConnected = uiState.isNetworkConnected,
                        modifier = Modifier.padding(innerPadding),
                        onCloseClick = ::finish,
                        onAddToCart = viewModel::addToCart,
                        onLastViewedProductClick = { lastViewedProduct ->
                            startActivity(this, lastViewedProduct.id)
                        },
                        onIncreaseQuantity = viewModel::increaseQuantity,
                        onDecreaseQuantity = viewModel::decreaseQuantity,
                    )
                }
            }
        }
    }

    private fun parse(intent: Intent): Long? =
        if (intent.hasExtra(PUT_EXTRA_KEY_PRODUCT_ID)) {
            (intent.getLongExtra(PUT_EXTRA_KEY_PRODUCT_ID, 0L))
        } else {
            null
        }
}
