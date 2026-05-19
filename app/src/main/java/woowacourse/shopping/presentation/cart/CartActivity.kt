package woowacourse.shopping.presentation.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.presentation.cart.ui.CartScreen
import woowacourse.shopping.presentation.cart.viewmodel.CartViewModel
import woowacourse.shopping.presentation.recommend.RecommendActivity
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class CartActivity : ComponentActivity() {
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidshoppingTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                CartScreen(
                    uiState = uiState,
                    isSelectedAll = uiState.isSelectAll,
                    onBack = { finish() },
                    onNextPage = { viewModel.nextPage() },
                    onPreviousPage = { viewModel.previousPage() },
                    onDeleteItem = { viewModel.deleteItem(it) },
                    onIncrease = { viewModel.addItemToCart(it) },
                    onDecrease = { viewModel.removeItemFromCart(it) },
                    onSelected = { viewModel.selectItem(it) },
                    onOrderClick = {
                        startActivity(
                            RecommendActivity.newIntent(
                                context,
                                viewModel.getPaymentItemIds(),
                            ),
                        )
                    },
                    onSelectAll = { viewModel.toggleSelectAll() },
                )
            }
        }
    }
}
