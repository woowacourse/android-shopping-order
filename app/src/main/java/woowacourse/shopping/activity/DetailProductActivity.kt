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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.BackendViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.DetailProductScreen
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class DetailProductActivity : ComponentActivity() {
    private val backendViewModelFactory: BackendViewModelFactory by lazy { BackendViewModelFactory() }
    private val productViewModel: ProductViewModel by viewModels { backendViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { backendViewModelFactory }

    companion object {
        private const val EXTRA_PRODUCT_ID = "productId"
        private const val EXTRA_SHOW_LAST_VIEWED = "showLastViewed"
        private const val INVALID_PRODUCT_ID = -1L
        private const val DEFAULT_QUANTITY = 1

        fun start(
            context: Context,
            productId: Long,
            showLastViewed: Boolean = true,
        ) {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, productId)
            intent.putExtra(EXTRA_SHOW_LAST_VIEWED, showLastViewed)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as ShoppingApplication).appContainer
        val productId = intent.extras?.getLong(EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID) ?: INVALID_PRODUCT_ID
        if (productId == INVALID_PRODUCT_ID) {
            finish()
            return
        }
        val showLastViewedSection = intent.extras?.getBoolean(EXTRA_SHOW_LAST_VIEWED, true) ?: true
        val lastViewedProductId =
            if (showLastViewedSection) {
                resolveLastViewedProductId(
                    currentProductId = productId,
                    recentVisitedProductIds = appContainer.visitStore.recentVisitedProductIds.value,
                )
            } else {
                null
            }

        shoppingCartViewModel.requestCartItems()
        productViewModel.requestProductDetail(productId)
        if (lastViewedProductId != null && lastViewedProductId != productId) {
            productViewModel.requestProductDetail(lastViewedProductId)
        }
        lifecycleScope.launch {
            appContainer.visitStore.visit(productId)
        }

        setContent {
            val productDetails by productViewModel.productDetails.collectAsStateWithLifecycle()
            var selectedQuantity by rememberSaveable { mutableIntStateOf(DEFAULT_QUANTITY) }
            val currentProduct = productDetails[productId]
            val shoppingItem =
                currentProduct?.let { product ->
                    ShoppingItem(
                        product = product,
                        quantity = 0,
                    )
                }
            val lastViewedShoppingItem =
                lastViewedProductId
                    ?.takeIf { id -> id != productId }
                    ?.let { id -> productDetails[id] }
                    ?.let { product ->
                        ShoppingItem(
                            product = product,
                            quantity = 0,
                        )
                    }

            LaunchedEffect(productId) {
                selectedQuantity = DEFAULT_QUANTITY
            }

            AndroidShoppingTheme {
                if (shoppingItem != null) {
                    DetailProductScreen(
                        shoppingItem = shoppingItem,
                        lastViewedShoppingItem = lastViewedShoppingItem,
                        onAddToCartClick = {
                            shoppingCartViewModel.addOrIncreaseByProductId(
                                productId = productId,
                                amount = selectedQuantity,
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
                        quantity = selectedQuantity,
                        quantityPrice = shoppingItem.getProductQuantityPrice(selectedQuantity),
                        onQuantityPlusClick = {
                            selectedQuantity += 1
                        },
                        onQuantityMinusClick = {
                            if (selectedQuantity > DEFAULT_QUANTITY) {
                                selectedQuantity -= 1
                            }
                        },
                    )
                } else {
                    Text(stringResource(R.string.product_not_found_message))
                }
            }
        }
    }

    private fun resolveLastViewedProductId(
        currentProductId: Long,
        recentVisitedProductIds: List<Long>,
    ): Long? {
        if (recentVisitedProductIds.isEmpty()) return null
        val currentProductIndex = recentVisitedProductIds.indexOf(currentProductId)
        return when {
            currentProductIndex == 0 -> null
            currentProductIndex > 0 -> recentVisitedProductIds[currentProductIndex - 1]
            else -> recentVisitedProductIds.firstOrNull()
        }
    }
}
