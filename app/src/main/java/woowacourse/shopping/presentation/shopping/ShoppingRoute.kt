package woowacourse.shopping.presentation.shopping

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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import woowacourse.shopping.presentation.navigation.CartScreen
import woowacourse.shopping.presentation.navigation.DetailScreen
import woowacourse.shopping.presentation.navigation.SettingScreen
import woowacourse.shopping.presentation.shopping.ui.ShoppingScreen
import woowacourse.shopping.presentation.shopping.viewmodel.ShoppingEvent
import woowacourse.shopping.presentation.shopping.viewmodel.ShoppingViewModel

@Composable
fun ShoppingRoute(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is ShoppingEvent.ShowError -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.currentStateFlow
            .filter { it == Lifecycle.State.RESUMED }
            .collect {
                viewModel.loadCartItemQuantities()
                viewModel.loadRecentProducts(10)
            }
    }

    ShoppingScreen(
        uiState = uiState,
        onNavigateToCart = { navController.navigate(CartScreen) },
        onNavigateToSetting = { navController.navigate(SettingScreen) },
        onProductCardClick = { id -> navController.navigate(DetailScreen(productId = id)) },
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onUpsertRecentProduct = viewModel::upsertRecentProduct,
        onLoadMore = viewModel::loadMore,
    )
}
