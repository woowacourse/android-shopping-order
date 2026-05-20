package woowacourse.shopping.ui.productdetail

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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.common.theme.ShoppingTheme

class ProductDetailActivity : ComponentActivity() {
    private val container by lazy {
        (application as ShoppingApplication).appContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: ProductDetailViewModel =
                        viewModel(
                            factory =
                                ProductDetailViewModel.provideFactory(
                                    productRepo = container.productRepository,
                                    cartRepo = container.cartRepository,
                                    recentProductRepo = container.recentProductRepository,
                                ),
                        )

                    ProductDetailScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onCloseClick = {
                            finish()
                        },
                        onAddToCartClick = ::finish,
                        onLastViewedProductClick = {
                            val intent =
                                newIntent(context = this, productId = it.id, isFromBanner = true)
                            startActivity(intent)
                            finish()
                        },
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "com.woowacourse.shopping.PRODUCT_ID"
        const val EXTRA_IS_FROM_BANNER = "com.woowacourse.shopping.IS_FROM_BANNER"

        fun newIntent(
            context: Context,
            productId: Long,
            isFromBanner: Boolean = false,
        ): Intent = Intent(context, ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_PRODUCT_ID, productId)
            putExtra(EXTRA_IS_FROM_BANNER, isFromBanner)
        }
    }
}
