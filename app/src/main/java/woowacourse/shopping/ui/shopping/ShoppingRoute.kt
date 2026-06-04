package woowacourse.shopping.ui.shopping

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShoppingRoute(
    onCartClick: () -> Unit,
    onSettingClick: () -> Unit,
    onDetailClick: (String) -> Unit,
    viewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ShoppingUiEvent.NavToCart -> {
                    onCartClick()
                }

                ShoppingUiEvent.NavToSetting -> {
                    onSettingClick()
                }

                is ShoppingUiEvent.NavToDetail -> {
                    onDetailClick(event.productId)
                }

                is ShoppingUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
        onSettingClick = viewModel::navToSetting,
    )
}
