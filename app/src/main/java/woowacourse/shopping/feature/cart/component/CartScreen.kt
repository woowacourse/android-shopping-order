package woowacourse.shopping.feature.cart.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.feature.cart.CartEvent
import woowacourse.shopping.feature.cart.CartUiState
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.common.state.ProductUiModel

@Composable
fun CartScreen(
    onCloseClick: () -> Unit,
    activityFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.initialLoading()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CartEvent.FatalError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    activityFinish()
                }
            }
        }
    }
    CartScreenContent(
        uiState = uiState,
        onCloseClick = onCloseClick,
        onDelete = viewModel::deleteCartItem,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onPrev = viewModel::moveToPreviousPage,
        onNext = viewModel::moveToNextPage,
        canPrev = !viewModel.isStartPage(),
        canNext = !viewModel.isEndPage(),
        onChecked = viewModel::cartItemCheck,
        modifier = modifier,
    )
}

@Composable
fun CartScreenContent(
    uiState: CartUiState,
    onChecked: (String) -> Unit,
    onCloseClick: () -> Unit,
    onDelete: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    canPrev: Boolean,
    canNext: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            CartAppBar(onCloseClick = onCloseClick)
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .background(Color(0xff555555)),
            ) {
            }
        },
    ) { innerPadding ->
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
            ) {
                CartItemList(
                    checkMap = uiState.checkMap,
                    isLoading = uiState.isLoading,
                    cartContents = uiState.paginatedCartContents,
                    modifier = Modifier.weight(1f),
                    onDelete = onDelete,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease,
                    onChecked = onChecked,
                )
                Spacer(modifier = Modifier.height(40.dp))
                PageNavigator(
                    page = uiState.page,
                    onLeftClick = onPrev,
                    onRightClick = onNext,
                    canMoveToPreviousPage = canPrev,
                    canMoveToNextPage = canNext,
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview
@Composable
private fun CartScreenContentPreview() {
    CartScreenContent(
        uiState = CartUiState(
            isLoading = false,
            paginatedCartContents = MockData.MOCK_PRODUCTS.take(2).map {
                ProductUiModel(
                    name = it.name,
                    price = it.priceAmount(),
                    imageUrl = it.imageUrl,
                    id = it.id,
                    quantity = 1,
                )
            },
        ),
        onDelete = {},
        onIncrease = {},
        onDecrease = {},
        onPrev = {},
        onNext = {},
        canPrev = true,
        canNext = true,
        onCloseClick = {},
        onChecked = { _ -> },
    )
}
