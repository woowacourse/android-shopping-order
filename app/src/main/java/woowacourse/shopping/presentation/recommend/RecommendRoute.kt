package woowacourse.shopping.presentation.recommend

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.presentation.recommend.ui.RecommendScreen
import woowacourse.shopping.presentation.recommend.viewmodel.RecommendEvent
import woowacourse.shopping.presentation.recommend.viewmodel.RecommendViewModel

@Composable
fun RecommendRoute(
    productIds: List<Long>,
    navController: NavController,
    viewModel: RecommendViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadPaymentId(productIds.toLongArray())
        viewModel.loadRecommendProducts()
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is RecommendEvent.ShowError -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    RecommendScreen(
        uiState = uiState,
        onBack = navController::popBackStack,
        onOrderClick = {},
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
    )
}
