package woowacourse.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.component.route.ProductDetailRoute
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.viewmodel.ProductDetailViewModel
import woowacourse.shopping.ui.viewmodel.ProductDetailViewModelFactory
import kotlin.jvm.java

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductDetailRoute(
                        viewModel = viewModel,
                        onClose = { finish() },
                        onNavigateToProductDetail = { productId ->
                            startActivity(
                                context = this,
                                selectedProductId = productId,
                                lastViewedProductId = null,
                            )
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    companion object {
        const val SELECTED_PRODUCT_ID_KEY = "selected_product"
        const val LATEST_VIEWED_PRODUCT_ID_KEY = "latest_viewed_product"

        fun startActivity(
            context: Context,
            selectedProductId: Long,
            lastViewedProductId: Long?,
        ) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(SELECTED_PRODUCT_ID_KEY, selectedProductId)
            intent.putExtra(LATEST_VIEWED_PRODUCT_ID_KEY, lastViewedProductId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }
}
