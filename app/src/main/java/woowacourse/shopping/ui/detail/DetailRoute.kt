package woowacourse.shopping.ui.detail

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DetailRoute(
    productId: String,
    onCartClick: () -> Unit,
    onDismiss: () -> Unit,
    onDetailClick: (String) -> Unit,
    viewModel: DetailViewModel =
        viewModel(
            factory =
                DetailViewModel.Factory(
                    productId = productId,
                ),
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                DetailUiEvent.Dismiss -> {
                    onDismiss()
                }

                DetailUiEvent.NavToCart -> {
                    onCartClick()
                }

                is DetailUiEvent.NavToDetail -> {
                    onDetailClick(event.productId)
                }

                is DetailUiEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DetailScreen(
        uiState = uiState,
        onCloseClick = viewModel::onDismiss,
        onQuantityChange = viewModel::updateQuantity,
        onAddToCart = viewModel::addToCart,
        onRecentItemClick = viewModel::navToDetail,
    )
}
