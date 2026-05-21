package woowacourse.shopping.presentation.shopping

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import woowacourse.shopping.presentation.navigation.CartScreen
import woowacourse.shopping.presentation.navigation.DetailScreen
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

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadCartItemQuantities()
                    viewModel.loadRecentProducts(10)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ShoppingScreen(
        uiState = uiState,
        onNavigateToCart = { navController.navigate(CartScreen) },
        onProductCardClick = { id -> navController.navigate(DetailScreen(productId = id)) },
        onIncrease = { viewModel.increase(it) },
        onDecrease = { viewModel.decrease(it) },
        onUpsertRecentProduct = { viewModel.upsertRecentProduct(it) },
        onLoadMore = { viewModel.loadMore() },
    )
}
