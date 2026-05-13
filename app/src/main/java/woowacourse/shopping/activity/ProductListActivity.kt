@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ApiViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.ui.ProductListScreen
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.ProductListViewModel
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory

class ProductListActivity : ComponentActivity() {
    private val appContainer by lazy { (application as ShoppingApplication).appContainer }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(
            shoppingCartRepository = appContainer.shoppingCartRepository,
            shoppingItemRepository = appContainer.shoppingItemRepository,
            visitStore = appContainer.visitStore,
            networkStatusMonitor = appContainer.networkStatusMonitor,
        )
    }
    private val apiViewModelFactory: ApiViewModelFactory by lazy { ApiViewModelFactory() }
    private val productListViewModel: ProductListViewModel by viewModels { screenViewModelFactory }
    private val productViewModel: ProductViewModel by viewModels { apiViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeApiViewModels()
        requestApiData()
        setContent {
            val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                productListViewModel.event.collect { event ->
                    when (event) {
                        is ProductListViewModel.ProductListEvent.NavigateToDetailProduct ->
                            DetailProductActivity.start(
                                context = this@ProductListActivity,
                                productId = event.productId,
                                showLastViewed = event.showLastViewed,
                            )

                        ProductListViewModel.ProductListEvent.NavigateToShoppingCart ->
                            ShoppingCartActivity.start(this@ProductListActivity)
                    }
                }
            }
            AndroidShoppingTheme {
                ProductListScreen(
                    shoppingItems = uiState.shoppingItems,
                    recentViewedShoppingItems = uiState.recentViewedShoppingItems,
                    shoppingCartTotalCount = uiState.shoppingCartTotalCount,
                    isNetworkConnected = uiState.isNetworkConnected,
                    onAddToCartClick = { shoppingItem ->
                        productListViewModel.addProductToCart(shoppingItem)
                        shoppingCartViewModel.addOrIncreaseByProductId(shoppingItem.getProductId())
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        productListViewModel.increaseProductQuantity(shoppingItem)
                        shoppingCartViewModel.addOrIncreaseByProductId(shoppingItem.getProductId())
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        productListViewModel.decreaseProductQuantity(shoppingItem)
                        shoppingCartViewModel.decreaseByProductId(shoppingItem.getProductId())
                    },
                    onProductClick = productListViewModel::onProductClick,
                    onRecentViewedProductClick = productListViewModel::onRecentViewedProductClick,
                    onNavigateToCartClick = productListViewModel::onNavigateToCartClick,
                    bottomContent =
                        if (uiState.canLoadNextPage) {
                            {
                                MoreButton(
                                    onClick = productListViewModel::loadNextPage,
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
        requestApiData()
    }

    private fun requestApiData() {
        val recommendedCategory = appContainer.recommendationStore.recommendedCategory.value
        productViewModel.requestProduct(
            page = INITIAL_PAGE,
            size = PRODUCT_PAGE_SIZE,
            sort = PRODUCT_SORT,
            category = recommendedCategory,
        )
        shoppingCartViewModel.requestCartItems()
    }

    private fun observeApiViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    productViewModel.products.collect { products ->
                        appContainer.remoteShoppingStateSyncer.syncProducts(products)
                    }
                }
                launch {
                    shoppingCartViewModel.shoppingCartItems.collect { shoppingCartItems ->
                        if (!shoppingCartViewModel.hasLoadedCartItems.value) {
                            return@collect
                        }
                        appContainer.remoteShoppingStateSyncer.syncCartItems(shoppingCartItems)
                    }
                }
            }
        }
    }

    private companion object {
        private const val INITIAL_PAGE = 0
        private const val PRODUCT_PAGE_SIZE = 100
        private val PRODUCT_SORT = listOf("id,asc")
    }
}
