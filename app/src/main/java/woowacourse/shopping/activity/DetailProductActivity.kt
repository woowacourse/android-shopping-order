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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.screen.DetailProductScreen
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.DetailProductViewModel
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory

class DetailProductActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(
            appContainer = app.appContainer,
            retrofitService = app.retrofitService,
        )
    }
    private val detailProductViewModel: DetailProductViewModel by viewModels { screenViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val args = intent.toDetailProductArgs() ?: run {
            finish()
            return
        }

        detailProductViewModel.initialize(
            productId = args.productId,
            showLastViewed = args.showLastViewed,
        )
        observeApiViewModel()
        detailProductViewModel.loadProductDetail(args.productId)
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

    private fun observeApiViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailProductViewModel.uiState.collect { uiState ->
                        uiState.shoppingItem?.let { shoppingItem ->
                            app.appContainer.remoteShoppingStateSyncer.syncProduct(shoppingItem)
                        }
                    }
                }
            }
        }
    }

    data class DetailProductArgs(
        val productId: Long,
        val showLastViewed: Boolean,
    )

    companion object {
        private const val EXTRA_PRODUCT_ID = "productId"
        private const val EXTRA_SHOW_LAST_VIEWED = "showLastViewed"
        private const val INVALID_PRODUCT_ID = -1L

        fun start(
            context: Context,
            productId: Long,
            showLastViewed: Boolean = true,
        ) {
            val intent =
                Intent(context, DetailProductActivity::class.java).apply {
                    putExtra(EXTRA_PRODUCT_ID, productId)
                    putExtra(EXTRA_SHOW_LAST_VIEWED, showLastViewed)
                }
            context.startActivity(intent)
        }

        private fun Intent.toDetailProductArgs(): DetailProductArgs? {
            val productId = getLongExtra(EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID)
            if (productId == INVALID_PRODUCT_ID) return null

            return DetailProductArgs(
                productId = productId,
                showLastViewed = getBooleanExtra(EXTRA_SHOW_LAST_VIEWED, true),
            )
        }
    }
}
