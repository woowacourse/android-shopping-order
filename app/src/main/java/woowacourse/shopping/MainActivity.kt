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
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val lastViewedProductId by viewModel.lastViewProductId.collectAsStateWithLifecycle()

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatalogScreen(
                        catalog = uiState.products,
                        recentlyViewedProducts = uiState.recentlyViewedProducts,
                        onRecentlyViewedClick = { product ->
                            viewModel.updateHistory(product)
                            ProductDetailActivity.startActivity(
                                context = this,
                                selectedProductId = product.id,
                                lastViewedProductId = lastViewedProductId
                            )
                        },
                        onItemClick = { product ->
                            viewModel.updateHistory(product)
                            ProductDetailActivity.startActivity(
                                context = this,
                                selectedProductId = product.id,
                                lastViewedProductId = lastViewedProductId
                            )
                        },
                        onCartClick = {
                            CartActivity.startActivity(this)
                        },
                        onLoadClick = {
                            viewModel.loadMore()
                        },
                        modifier = Modifier.padding(innerPadding),
                        onAdd = { id, updateAmount ->
                            viewModel.updateCountWithID(id, updateAmount)
                        },
                        onMinus = { id, updateAmount ->
                            viewModel.updateCountWithID(id, updateAmount)
                        },
                        onDelete = { viewModel.removeWithID(it) },
                        onAddInCart = { viewModel.addToCart(it) },
                        isContainedInCart = { uiState.cart.isContain(it) },
                        specificProductCount = { uiState.cart.totalCountOfSpecificPurchaseProduct(it) },
                        totalCount = uiState.totalCartCount(),
                        isLoading = uiState.isLoading,
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
