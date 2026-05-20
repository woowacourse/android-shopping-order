package woowacourse.shopping.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DetailRoute(
    onDismiss: () -> Unit,
    onRecentItemClick: (String) -> Unit,
    navigateToCart: () -> Unit,
    showToastMessage: (String) -> Unit,
    viewModel: DetailViewModel =
        viewModel(
            factory =
                DetailViewModel.Factory,
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                DetailEvent.NavigateToCart -> {
                    navigateToCart()
                }

                DetailEvent.NavigateBack -> {
                    onDismiss()
                }

                DetailEvent.ShowProductNotFoundMessage -> {
                    showToastMessage("상품을 찾을 수 없습니다.")
                }

                DetailEvent.ShowProductLoadFailureMessage -> {
                    showToastMessage("상품 정보를 불러오지 못했습니다.")
                }

                DetailEvent.ShowAddCartFailureMessage -> {
                    showToastMessage("장바구니에 상품을 담지 못했습니다.")
                }
            }
        }
    }

    DetailScreen(
        uiState = uiState,
        onCloseClick = onDismiss,
        onQuantityChange = viewModel::updateQuantity,
        onAddToCart = viewModel::addToCart,
        onRecentItemClick = { onRecentItemClick(it) },
    )
}
