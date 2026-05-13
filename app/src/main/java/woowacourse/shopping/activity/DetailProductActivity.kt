@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ApiViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.ui.DetailProductScreen
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.DetailProductViewModel
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory

class DetailProductActivity : ComponentActivity() {
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
    private val detailProductViewModel: DetailProductViewModel by viewModels { screenViewModelFactory }
    private val productViewModel: ProductViewModel by viewModels { apiViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }

    companion object {
        private const val INVALID_PRODUCT_ID = -1L

        fun start(
            context: Context,
            productId: Long,
            showLastViewed: Boolean = true,
        ) {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra(DetailProductViewModel.EXTRA_PRODUCT_ID, productId)
            intent.putExtra(DetailProductViewModel.EXTRA_SHOW_LAST_VIEWED, showLastViewed)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        detailProductViewModel.initializeFromIntentExtras(intent.extras)
        observeApiViewModel()
        requestProductDetailFromApi()
        setContent {
            val uiState by detailProductViewModel.uiState.collectAsStateWithLifecycle()
            AndroidShoppingTheme {
                val shoppingItem = uiState.shoppingItem
                if (shoppingItem != null) {
                    DetailProductScreen(
                        shoppingItem = shoppingItem,
                        lastViewedShoppingItem = uiState.lastViewedShoppingItem,
                        onAddToCartClick = {
                            detailProductViewModel.addSelectedProductToCart()
                            shoppingCartViewModel.addOrIncreaseByProductId(
                                productId = shoppingItem.getProductId(),
                                amount = uiState.selectedQuantity,
                            )
                            finish()
                        },
                        onLastViewedProductClick = { selectedProductId ->
                            start(
                                context = this,
                                productId = selectedProductId,
                                showLastViewed = false,
                            )
                            finish()
                        },
                        onBackClick = this::finish,
                        quantity = uiState.selectedQuantity,
                        quantityPrice = uiState.quantityPrice,
                        onQuantityPlusClick = detailProductViewModel::increaseSelectedQuantity,
                        onQuantityMinusClick = detailProductViewModel::decreaseSelectedQuantity,
                    )
                } else {
                    Text(stringResource(R.string.product_not_found_message))
                }
            }
        }
    }

    private fun requestProductDetailFromApi() {
        val productId =
            intent.extras?.getLong(DetailProductViewModel.EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID)
                ?: INVALID_PRODUCT_ID
        if (productId == INVALID_PRODUCT_ID) {
            return
        }
        productViewModel.requestProductDetail(productId)
    }

    private fun observeApiViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    productViewModel.productDetails.collect { productDetails ->
                        val productId =
                            intent.extras?.getLong(DetailProductViewModel.EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID)
                                ?: INVALID_PRODUCT_ID
                        if (productId == INVALID_PRODUCT_ID) {
                            return@collect
                        }
                        val detailProduct = productDetails[productId] ?: return@collect
                        appContainer.remoteShoppingStateSyncer.syncProduct(detailProduct)
                    }
                }
            }
        }
    }
}
