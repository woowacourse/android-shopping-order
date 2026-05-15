@file:Suppress("FunctionName")

package woowacourse.shopping.ui.detail

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
import woowacourse.shopping.di.ApiViewModelFactory
import woowacourse.shopping.di.ScreenViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.productlist.ProductViewModel
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class DetailProductActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(
            appContainer = app.appContainer,
        )
    }
    private val apiViewModelFactory: ApiViewModelFactory by lazy {
        ApiViewModelFactory(
            app.retrofitService,
        )
    }
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
                        app.appContainer.remoteShoppingStateSyncer.syncProduct(detailProduct)
                    }
                }
            }
        }
    }
}
