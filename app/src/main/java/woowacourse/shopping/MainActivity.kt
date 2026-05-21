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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.component.route.ShoppingRoute
import woowacourse.shopping.ui.component.screen.CatalogScreen
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.viewmodel.ShoppingViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingViewModelFactory
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ShoppingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            viewModel =
                viewModel<ShoppingViewModel>(
                    factory =
                        ShoppingViewModelFactory(
                            (application as ShoppingApplication).cartRepository,
                            (application as ShoppingApplication).recentlyViewedProductRepository,
                            (application as ShoppingApplication).productRepository,
                        ),
                )

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingRoute(
                        viewModel = viewModel,
                        onNavigateToProductDetail = { productId, lastViewedId ->
                            ProductDetailActivity.startActivity(
                                context = this,
                                selectedProductId = productId,
                                lastViewedProductId = lastViewedId
                            )
                        },
                        onNavigateToCart = {
                            CartActivity.startActivity(this)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(::viewModel.isInitialized) {
            lifecycleScope.launch {
                viewModel.fetchCart()
            }
        }
    }
}
