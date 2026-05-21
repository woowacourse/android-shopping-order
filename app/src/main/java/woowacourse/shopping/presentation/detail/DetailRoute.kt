package woowacourse.shopping.presentation.detail

import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.presentation.detail.model.DetailUiState
import woowacourse.shopping.presentation.detail.ui.DetailScreen
import woowacourse.shopping.presentation.detail.viewmodel.DetailEvent
import woowacourse.shopping.presentation.detail.viewmodel.DetailViewModel
import woowacourse.shopping.presentation.navigation.CartScreen
import woowacourse.shopping.presentation.navigation.DetailScreen

@Composable
fun DetailRoute(
    productId: Long,
    isFromLastSeen: Boolean,
    navController: NavController,
    viewModel: DetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is DetailEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is DetailEvent.NavigateToCart -> {
                    navController.navigate(CartScreen)
                }
            }
        }
    }

    LaunchedEffect(productId, isFromLastSeen) {
        viewModel.loadProduct(productId, isFromLastSeen)
    }

    when (val state = uiState) {
        is DetailUiState.Loading -> CircularProgressIndicator()
        is DetailUiState.Error -> {}
        is DetailUiState.Success -> {
            DetailScreen(
                uiState = state,
                onClickLastProductCard = {
                    navController.navigate(
                        DetailScreen(
                            productId = it,
                            isFromLastSeen = true,
                        ),
                    ) {
                        popUpTo<DetailScreen> { inclusive = true }
                    }
                },
                onBack = navController::popBackStack,
                onAddToCart = {
                    viewModel.addToCart(
                        id = productId,
                        quantity = state.quantity,
                    )
                },
                onIncrease = { viewModel.increase() },
                onDecrease = { viewModel.decrease() },
            )
        }
    }
}
