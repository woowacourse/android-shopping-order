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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.theme.ShoppingTheme

class ProductDetailActivity : ComponentActivity() {
    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadProductFromIntent(intent)

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val product = uiState.product ?: return@Scaffold

                    ProductDetailScreen(
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        loadProductFromIntent(intent)
    }

    private fun loadProductFromIntent(intent: Intent) {
        val productId = parse(intent)
        if (productId == null) {
            finish()
            return
        }
        viewModel.loadProduct(productId)
    }

    private fun parse(intent: Intent): Long? =
        if (intent.hasExtra(PUT_EXTRA_KEY_PRODUCT_ID)) {
            (intent.getLongExtra(PUT_EXTRA_KEY_PRODUCT_ID, 0L))
        } else {
            null
        }

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
}
