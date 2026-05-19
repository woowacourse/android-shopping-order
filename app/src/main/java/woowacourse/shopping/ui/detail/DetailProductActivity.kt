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
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class DetailProductActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            appContainer = app.appContainer,
        )
    }

    private val detailProductViewModel: DetailProductViewModel by viewModels { viewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { viewModelFactory }

    companion object {
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
                                onSuccess = this::finish,
                            )
                        },
                        onLastViewedProductClick = { selectedProductId ->
                            start(
                                context = this,
                                productId = selectedProductId,
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
}
