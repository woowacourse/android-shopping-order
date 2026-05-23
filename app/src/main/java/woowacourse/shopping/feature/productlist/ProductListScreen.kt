package woowacourse.shopping.feature.productlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.feature.productlist.component.ProductList
import woowacourse.shopping.feature.productlist.component.ProductListAppBar
import woowacourse.shopping.feature.productlist.component.RecentProductList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    vm: ProductListViewModel = viewModel(factory = ProductListViewModel.Factory),
    onProductClick: (Long, Long?) -> Unit,
    onSettingClick: () -> Unit,
    onCartIconClick: () -> Unit,
    activityFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                is ProductListEvent.FatalError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    activityFinish()
                }
            }
        }
    }

    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            ProductListAppBar(
                onSettingClick = onSettingClick,
                onCartIconClick = onCartIconClick,
                cartQuantities = state.cartTotalQuantity,
            )
        },
    ) { innerPadding ->
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            vm.cartRefresh()
            vm.loadRecentProducts()
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
        ) {
            Box(
                modifier =
                    Modifier
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
                            modifier =
                                Modifier
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
                                } else {
                                    null
                                },
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
        onSettingClick = {},
        onCartIconClick = { },
        activityFinish = { },
    )
}
