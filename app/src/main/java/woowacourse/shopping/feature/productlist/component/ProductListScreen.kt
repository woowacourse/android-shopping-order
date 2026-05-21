package woowacourse.shopping.feature.productlist.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.feature.common.state.ErrorDialog
import woowacourse.shopping.feature.productlist.ProductListEvent
import woowacourse.shopping.feature.productlist.ProductListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    vm: ProductListViewModel = viewModel(factory = ProductListViewModel.Factory),
    onProductClick: (String, String?) -> Unit,
    onCartIconClick: () -> Unit,
    activityFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.initialLoading()
        vm.event.collect { event ->
            when (event) {
                is ProductListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    val state by vm.uiState.collectAsStateWithLifecycle()

    ErrorDialog(
        error = state.error,
        onDismiss = {
            vm.dismissError()
            activityFinish()
        },
        onRetry = {
            vm.dismissError()
            vm.initialLoading()
        },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            ProductListAppBar(
                onCartIconClick = onCartIconClick,
                cartQuantities = state.cartTotalQuantity,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                Column {
                    if (state.recentProducts.isNotEmpty()) {
                        RecentProductList(
                            recentProducts = state.recentProducts,
                            onRecentProductClick = {
                                vm.insertRecentProduct(it.id)
                                onProductClick(
                                    it.id,
                                    state.mostRecentProductId,
                                )
                            },
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(Color(0xff555555)),
                        )
                    }

                    ProductList(
                        isLoading = state.isLoading,
                        products = state.productUiModels,
                        onProductClick = {
                            vm.insertRecentProduct(it.id)
                            onProductClick(
                                it.id,
                                if (state.recentProducts.isNotEmpty()) {
                                    state.recentProducts.first().id
                                } else null,
                            )
                        },
                        onLoading = vm::loadingFetch,
                        onIncrease = vm::increase,
                        onDecrease = vm::decrease,
                        isEnd = state.isEnd,
                        modifier = Modifier.padding(20.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProductListScreen() {
    ProductListScreen(
        onProductClick = { _, _ -> },
        onCartIconClick = { },
        activityFinish = { },
    )
}
