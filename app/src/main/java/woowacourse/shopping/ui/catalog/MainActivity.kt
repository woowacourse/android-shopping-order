package woowacourse.shopping.ui.catalog

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
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ShoppingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            viewModel = viewModel<ShoppingViewModel>(
                factory =
                    ShoppingViewModelFactory(
                        (application as ShoppingApplication).cartRepository,
                        (application as ShoppingApplication).recentlyViewedProductRepository,
                        (application as ShoppingApplication).productRepository,
                    ),
            )
            val cartState by viewModel.cart.collectAsStateWithLifecycle()
            val viewHistory by viewModel.recentlyViewedProducts.collectAsStateWithLifecycle()
            val currentProducts by viewModel.products.collectAsStateWithLifecycle()
            val lastViewedProduct by viewModel.lastViewProductId.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    CatalogScreen(
                        catalog = currentProducts,
                        recentlyViewedProducts = viewHistory,
                        onRecentlyViewedClick = { product ->
                            viewModel.updateHistory(product)
                            val intent =
                                Intent(this, ProductDetailActivity::class.java).apply {
                                    putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, product.id)
                                    putExtra(
                                        IntentKeys.LATEST_VIEWED_PRODUCT_ID_KEY,
                                        lastViewedProduct
                                    )
                                }
                            startActivity(intent)
                        },
                        onItemClick = { product ->
                            viewModel.updateHistory(product)
                            val intent =
                                Intent(this, ProductDetailActivity::class.java).apply {
                                    putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, product.id)
                                    putExtra(
                                        IntentKeys.LATEST_VIEWED_PRODUCT_ID_KEY,
                                        lastViewedProduct
                                    )
                                }
                            startActivity(intent)
                        },
                        onCartClick = {
                            val intent = Intent(this, CartActivity::class.java)
                            startActivity(intent)
                        },
                        onLoadClick = {
                            viewModel.loadMore()
                        },
                        modifier = Modifier.Companion.padding(innerPadding),
                        onAdd = { id, updateAmount ->
                            viewModel.updateCountWithID(id, updateAmount)
                        },
                        onMinus = { id, updateAmount ->
                            viewModel.updateCountWithID(id, updateAmount)
                        },
                        onDelete = { viewModel.removeWithID(it) },
                        onAddInCart = { viewModel.addToCart(it) },
                        isContainedInCart = { cartState.isContain(it) },
                        specificProductCount = { cartState.totalCountOfSpecificPurchaseProduct(it) },
                        totalCount = cartState.totalCount(),
                        isLoading = isLoading,
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(::viewModel.isInitialized) {
            viewModel.fetchCart()
        }
    }
}
