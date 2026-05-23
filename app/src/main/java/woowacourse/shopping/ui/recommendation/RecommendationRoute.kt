package woowacourse.shopping.ui.recommendation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import woowacourse.shopping.ui.navigation.Payment
import woowacourse.shopping.ui.navigation.ProductDetail
import woowacourse.shopping.ui.navigation.Shopping

@Composable
fun RecommendationRoute(
    viewModel: RecommendationViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val composableScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var snackbarJob: Job? = null
            viewModel.event.collect { event ->
                when (event) {
                    is RecommendationEvent.SnackbarEvent -> {
                        snackbarJob?.cancel()
                        snackbarJob = launch {
                            snackbarHostState.showSnackbar(
                                event.errorMsg
                            )
                        }
                    }

                    is RecommendationEvent.AddToCart ->
                        viewModel.addToCart(event.purchaseProduct)

                    is RecommendationEvent.UpdateAmount ->
                        viewModel.updateCountWithID(
                            id = event.targetID,
                            updateAmount = event.updateAmount
                        )

                    is RecommendationEvent.RemoveFromCart ->
                        viewModel.removeWithID(event.targetId)

                    is RecommendationEvent.NavigateToCart ->
                        navController.popBackStack()

                    is RecommendationEvent.NavigateToPayment ->
                        navController.navigate(
                            Payment(
                                checkedIds = event.checkedIds
                            )
                        )
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        composableScope.launch {
            viewModel.fetchCart()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        CartRecommendationScreen(
            recommendedProducts = uiState.recommendedProducts,
            totalPrice = uiState.totalPrice,
            totalCount = uiState.checkedIds.size,
            onBackClick = viewModel::navigateToCart,
            onOrderClick = { viewModel.navigateToPayment(uiState.checkedIds) },
            onAddInCart = viewModel::addToCartTrigger,
            onAdd = viewModel::updateAmountTrigger,
            onMinus = viewModel::updateAmountTrigger,
            onDelete = viewModel::removeFromCartTrigger,
            isContainedInCart = { id -> uiState.cart.isContain(id) },
            itemCount = { id -> uiState.cart.totalCountOfSpecificPurchaseProduct(id) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
