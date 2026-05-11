@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.ProductListScreen
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.viewmodel.ProductListViewModel

class ProductListActivity : ComponentActivity() {
    private val productListViewModel: ProductListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
            AndroidShoppingTheme {
                ProductListScreen(
                    shoppingItems = uiState.shoppingItems,
                    recentViewedShoppingItems = uiState.recentViewedShoppingItems,
                    shoppingCartTotalCount = uiState.shoppingCartTotalCount,
                    isNetworkConnected = uiState.isNetworkConnected,
                    onAddToCartClick = { shoppingItem ->
                        productListViewModel.addProductToCart(shoppingItem)
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        productListViewModel.increaseProductQuantity(shoppingItem)
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        productListViewModel.decreaseProductQuantity(shoppingItem)
                    },
                    onProductClick = { productId ->
                        DetailProductActivity.start(this, productId)
                    },
                    onRecentViewedProductClick = { productId ->
                        DetailProductActivity.start(this, productId)
                    },
                    onNavigateToCartClick = {
                        ShoppingCartActivity.start(this)
                    },
                    bottomContent =
                        if (uiState.canLoadNextPage) {
                            {
                                MoreButton(
                                    onClick = {
                                        productListViewModel.loadNextPage()
                                    },
                                )
                            }
                        } else {
                            null
                        },
                )
            }
        }
    }
}
