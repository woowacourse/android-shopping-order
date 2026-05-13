package woowacourse.shopping.ui.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.theme.ShoppingTheme

class CartActivity : ComponentActivity() {
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CartScreen(
                        cartListState = uiState.cartListState,
                        isNetworkConnected = uiState.isNetworkConnected,
                        modifier = Modifier.padding(innerPadding),
                        onBackClick = ::finish,
                        onOrderClick = {},
                        onItemCheckedChange = viewModel::toggleItemSelection,
                        onDeleteClick = viewModel::delete,
                        onIncreaseQuantity = viewModel::increaseQuantity,
                        onDecreaseQuantity = viewModel::decreaseQuantity,
                        onPreviousClick = viewModel::loadPreviousPage,
                        onNextClick = viewModel::loadNextPage,
                    )
                }
            }
        }
    }
}
