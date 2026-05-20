package woowacourse.shopping.presentation.shopping

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.detail.DetailActivity
import woowacourse.shopping.presentation.shopping.ui.ShoppingScreen
import woowacourse.shopping.presentation.shopping.viewmodel.ShoppingEvent
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
                val lifecycleOwner = LocalLifecycleOwner.current

                LaunchedEffect(Unit) {
                    viewModel.initialize()
                }

                LaunchedEffect(viewModel.uiState, lifecycleOwner) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiEvents.collect { event ->
                            when (event) {
                                is ShoppingEvent.ShowError -> {
                                    Toast.makeText(this@ShoppingActivity, event.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                ShoppingScreen(
                    uiState = uiState,
                    onNavigateToCart = {
                        val intent = Intent(this, CartActivity::class.java)
                        startActivity(intent)
                    },
                    onProductCardClick = { startActivity(DetailActivity.newIntent(this, it)) },
                    onIncrease = { viewModel.increase(it) },
                    onDecrease = { viewModel.decrease(it) },
                    onUpsertRecentProduct = { viewModel.upsertRecentProduct(it) },
                    onLoadMore = { viewModel.loadMore() },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItemQuantities()
        viewModel.loadRecentProducts(10)
    }
}
