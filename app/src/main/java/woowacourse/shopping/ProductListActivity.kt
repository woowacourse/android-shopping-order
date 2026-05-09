@file:Suppress("FunctionName")

package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import woowacourse.shopping.ui.ProductListScreen
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.pagination.ProductPageStateHolder
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.viewmodel.ProductListViewModel

@OptIn(ExperimentalMaterial3Api::class)
class ProductListActivity : ComponentActivity() {
    private val productListViewModel: ProductListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shoppingItems by productListViewModel.shoppingItems.collectAsState()
            var savedCurrentPage by rememberSaveable { mutableIntStateOf(0) }
            AndroidShoppingTheme {
                val productPageStateHolder =
                    remember(shoppingItems, savedCurrentPage) {
                        ProductPageStateHolder(
                            shoppingItems = shoppingItems,
                            initialPage = savedCurrentPage,
                        )
                    }
                ProductListScreen(
                    shoppingItems = productPageStateHolder.getItems(),
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
                    onNavigateToCartClick = {
                        ShoppingCartActivity.start(this)
                    },
                ) {
                    MoreButton(
                        onClick = {
                            productPageStateHolder.nextPage()
                            savedCurrentPage = productPageStateHolder.currentPage
                        },
                    )
                }
            }
        }
    }
}
