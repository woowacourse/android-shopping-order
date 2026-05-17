package woowacourse.shopping.presentation.shopping

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.detail.DetailActivity
import woowacourse.shopping.presentation.shopping.ui.ShoppingScreen
import woowacourse.shopping.presentation.shopping.viewmodel.ShoppingViewModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class ShoppingActivity : ComponentActivity() {
    private val viewModel by viewModels<ShoppingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidshoppingTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ShoppingScreen(
                    uiState = uiState,
                    onNavigateToCart = {
                        val intent = Intent(this, CartActivity::class.java)
                        startActivity(intent)
                    },
                    onProductCardClick = { startActivity(DetailActivity.newIntent(this, it)) },
                    onIncrease = { viewModel.addItemToCart(it) },
                    onDecrease = { viewModel.removeItemFromCart(it) },
                    onUpsertRecentProduct = { viewModel.upsertRecentProduct(it) },
                    onLoadMore = { viewModel.loadMoreProducts() },
                )
            }
        }
    }
}
