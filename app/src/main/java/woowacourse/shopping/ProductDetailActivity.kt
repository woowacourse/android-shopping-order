package woowacourse.shopping

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.component.screen.ProductDetailScreen
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.viewmodel.ProductDetailViewModel
import woowacourse.shopping.ui.viewmodel.ProductDetailViewModelFactory

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedProductId =
            intent.getLongExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, -1L).takeIf { it != -1L } ?: run {
                finish()
                return
            }

        val lastViewedProductId =
            if (intent.hasExtra(IntentKeys.LATEST_VIEWED_PRODUCT_ID_KEY)) {
                intent.getLongExtra(IntentKeys.LATEST_VIEWED_PRODUCT_ID_KEY, -1L).takeIf { it != -1L }
            } else {
                null
            }

        enableEdgeToEdge()
        setContent {
            val viewModel: ProductDetailViewModel =
                viewModel<ProductDetailViewModel>(
                    factory =
                        ProductDetailViewModelFactory(
                            cartRepository = (application as ShoppingApplication).cartRepository,
                            recentlyViewedProductRepository = (application as ShoppingApplication).recentlyViewedProductRepository,
                            productRepository = (application as ShoppingApplication).productRepository,
                            selectedProductId = selectedProductId,
                            lastViewedProductId = lastViewedProductId,
                        ),
                )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    uiState.product?.let {
                        ProductDetailScreen(
                            product = it,
                            count = uiState.count,
                            lastViewedProduct = uiState.lastViewProduct,
                            onLastViewedClick = {
                                val intent = Intent(this, ProductDetailActivity::class.java)
                                uiState.lastViewProduct?.run {
                                    viewModel.updateHistory(it)
                                    intent.putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, it.id)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                } ?: finish()
                            },
                            onAdd = { viewModel.addCount() },
                            onMinus = { viewModel.minusCount() },
                            onAddRequest = {
                                viewModel.addPurchaseProduct(PurchaseProduct(it.id, it, uiState.count))
                                finish()
                            },
                            onClose = { finish() },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
