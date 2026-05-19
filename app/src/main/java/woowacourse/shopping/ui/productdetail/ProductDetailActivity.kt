package woowacourse.shopping.ui.productdetail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

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

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    val count = viewModel.countState.collectAsStateWithLifecycle()
                    val selectedProduct = viewModel.selectedProduct.collectAsStateWithLifecycle()
                    val lastViewedProduct =
                        viewModel.lastViewedProduct.collectAsStateWithLifecycle()
                    selectedProduct.value?.let {
                        ProductDetailScreen(
                            product = it,
                            count = count.value,
                            lastViewedProduct = lastViewedProduct.value,
                            onLastViewedClick = {
                                val intent = Intent(this, ProductDetailActivity::class.java)
                                lastViewedProduct.value?.run {
                                    viewModel.updateHistory(it)
                                    intent.putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, it.id)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                } ?: finish()
                            },
                            onAdd = { viewModel.addCount() },
                            onMinus = { viewModel.minusCount() },
                            onAddRequest = {
                                viewModel.addPurchaseProduct(
                                    PurchaseProduct(
                                        it.id,
                                        it,
                                        count.value
                                    )
                                )
                                finish()
                            },
                            onClose = { finish() },
                            modifier = Modifier.Companion.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
