@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.BackendViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.ProductListScreen
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class ProductListActivity : ComponentActivity() {
    private val backendViewModelFactory: BackendViewModelFactory by lazy {
        val app = application as ShoppingApplication
        BackendViewModelFactory(app.retrofitService)
    }
    private val productViewModel: ProductViewModel by viewModels { backendViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { backendViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as ShoppingApplication).appContainer
        productViewModel.requestProduct(size = MAX_PRODUCT_SIZE)
        shoppingCartViewModel.requestCartItems()
        setContent {
            val state by productViewModel.state.collectAsStateWithLifecycle()
            val products by productViewModel.products.collectAsStateWithLifecycle()
            val shoppingCartItems by shoppingCartViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val recentVisitedProductIds by appContainer.visitStore.recentVisitedProductIds.collectAsStateWithLifecycle()
            val isNetworkConnected by appContainer.networkStatusMonitor.isConnected.collectAsStateWithLifecycle()
            val shoppingItems =
                remember(products, shoppingCartItems) {
                    createShoppingItems(
                        products = products,
                        shoppingCartItems = shoppingCartItems,
                    )
                }
            val recentViewedShoppingItems =
                remember(shoppingItems, recentVisitedProductIds) {
                    createRecentViewedShoppingItems(
                        shoppingItems = shoppingItems,
                        recentVisitedProductIds = recentVisitedProductIds,
                    )
                }
            var currentPage by rememberSaveable { mutableIntStateOf(INITIAL_PAGE) }
            val visibleProductCount = (currentPage + 1) * PAGE_ITEM_SIZE
            val visibleShoppingItems = shoppingItems.take(visibleProductCount)
            val canLoadNextPage = visibleProductCount < shoppingItems.size

            AndroidShoppingTheme {
                ProductListScreen(
                    shoppingItems = visibleShoppingItems,
                    recentViewedShoppingItems = recentViewedShoppingItems,
                    shoppingCartTotalCount = shoppingCartViewModel.getTotalCount(),
                    isNetworkConnected = isNetworkConnected,
                    state = state,
                    onAddToCartClick = { shoppingItem ->
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                            amount = 1,
                        )
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                            amount = 1,
                        )
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        shoppingCartViewModel.decreaseByProductId(shoppingItem.getProductId())
                    },
                    onProductClick = { productId ->
                        DetailProductActivity.start(
                            context = this@ProductListActivity,
                            productId = productId,
                            showLastViewed = true,
                        )
                    },
                    onRecentViewedProductClick = { productId ->
                        DetailProductActivity.start(
                            context = this@ProductListActivity,
                            productId = productId,
                            showLastViewed = false,
                        )
                    },
                    onNavigateToCartClick = {
                        ShoppingCartActivity.start(this@ProductListActivity)
                    },

                    bottomContent =
                        if (canLoadNextPage) {
                            {
                                MoreButton(
                                    onClick = {
                                        currentPage += 1
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

    override fun onResume() {
        super.onResume()
        productViewModel.requestProduct(size = MAX_PRODUCT_SIZE)
        shoppingCartViewModel.requestCartItems()
    }

    private fun createShoppingItems(
        products: List<Product>,
        shoppingCartItems: List<ShoppingCartItem>,
    ): List<ShoppingItem> {
        val shoppingItems = mutableListOf<ShoppingItem>()
        products.forEach { product ->
            val matchedCartItem =
                shoppingCartItems.firstOrNull { shoppingCartItem ->
                    shoppingCartItem.product.id == product.id
                }
            shoppingItems.add(
                ShoppingItem(
                    product = product,
                    quantity = matchedCartItem?.getQuantity() ?: 0,
                ),
            )
        }
        return shoppingItems
    }

    private fun createRecentViewedShoppingItems(
        shoppingItems: List<ShoppingItem>,
        recentVisitedProductIds: List<Long>,
    ): List<ShoppingItem> {
        val recentViewedShoppingItems = mutableListOf<ShoppingItem>()
        recentVisitedProductIds.forEach { productId ->
            val matchedShoppingItem =
                shoppingItems.firstOrNull { shoppingItem ->
                    shoppingItem.getProductId() == productId
                }
            if (matchedShoppingItem != null) {
                recentViewedShoppingItems.add(matchedShoppingItem)
            }
        }
        return recentViewedShoppingItems
    }

    private companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_ITEM_SIZE = 20
        private const val MAX_PRODUCT_SIZE = 100
    }
}
