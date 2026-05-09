@file:Suppress("FunctionName")

package woowacourse.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import woowacourse.shopping.ui.DetailProductScreen
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.viewmodel.DetailProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
class DetailProductActivity : ComponentActivity() {
    private val detailProductViewModel: DetailProductViewModel by viewModels()

    companion object {
        private const val EXTRA_PRODUCT_ID = "productId"
        private const val INVALID_PRODUCT_ID = -1L

        fun start(
            context: Context,
            productId: Long,
        ) {
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, productId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val productId = intent.getLongExtra(EXTRA_PRODUCT_ID, INVALID_PRODUCT_ID)
        setContent {
            val shoppingItems by detailProductViewModel.shoppingItems.collectAsState()
            var selectedQuantity by rememberSaveable(productId) {
                mutableIntStateOf(detailProductViewModel.defaultQuantity())
            }
            val shoppingItem =
                if (productId == INVALID_PRODUCT_ID) {
                    null
                } else {
                    shoppingItems.find { item -> item.getProductId() == productId }
                }
            AndroidShoppingTheme {
                if (shoppingItem != null) {
                    DetailProductScreen(
                        shoppingItem = shoppingItem,
                        onAddToCartClick = { selectedShoppingItem ->
                            detailProductViewModel.addProductToCart(selectedShoppingItem, selectedQuantity)
                            this.finish()
                        },
                        onBackClick = this::finish,
                        quantity = selectedQuantity,
                        quantityPrice = detailProductViewModel.quantityPrice(shoppingItem, selectedQuantity),
                        onQuantityPlusClick = { selectedQuantity += 1 },
                        onQuantityMinusClick = {
                            if (selectedQuantity > 1) {
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
}
