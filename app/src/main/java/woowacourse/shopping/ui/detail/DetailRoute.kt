package woowacourse.shopping.ui.detail

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
fun DetailRoute(
    navController: NavController,
    viewModel: DetailViewModel =
        viewModel(
            factory =
                DetailViewModel.Factory,
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                DetailEvent.NavigateToCart -> {
                    navController.navigate(Cart)
                }

                DetailEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                DetailEvent.ShowProductNotFoundMessage -> {
                    Toast.makeText(context, "상품을 찾을 수 없습니다.", Toast.LENGTH_SHORT)
                }

                DetailEvent.ShowProductLoadFailureMessage -> {
                    Toast.makeText(context, "상품 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT)
                }

                DetailEvent.ShowAddCartFailureMessage -> {
                    Toast.makeText(context, "장바구니에 상품을 담지 못했습니다.", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    DetailScreen(
        uiState = uiState,
        onCloseClick = { navController.popBackStack() },
        onQuantityChange = viewModel::updateQuantity,
        onAddToCart = viewModel::addToCart,
        onRecentItemClick = { navController.navigate(Detail(productId = it)) },
    )
}
