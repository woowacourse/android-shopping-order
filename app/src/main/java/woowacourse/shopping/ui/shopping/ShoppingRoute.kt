package woowacourse.shopping.ui.shopping

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.ui.nav.Cart
import woowacourse.shopping.ui.nav.Detail

@Composable
fun ShoppingRoute(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ShoppingUiEvent.NavToCart -> {
                    navController.navigate(Cart)
                }

                is ShoppingUiEvent.NavToDetail -> {
                    navController.navigate(Detail(productId = event.productId))
                }

                is ShoppingUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    ShoppingScreen(
        uiState = uiState,
        onLoad = viewModel::loadMore,
        onProductClick = { viewModel.onProductClick(it) },
        onCartClick = { viewModel.onCartClick() },
        onQuantityChange = viewModel::updateQuantity,
    )
}
