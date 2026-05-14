package woowacourse.shopping.feature.cart.component

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import woowacourse.shopping.feature.cart.CartEvent
import woowacourse.shopping.feature.cart.CartViewModel

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

    Scaffold(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            CartAppBar(onCloseClick = onCloseClick)
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
                    isLoading = uiState.isLoading,
                    uiState.paginatedCartContents,
                    modifier = Modifier.weight(1f),
                    onDelete = viewModel::deleteCartItem,
                    onIncrease = { viewModel.increase(it) },
                    onDecrease = { viewModel.decrease(it) },
                )
                Spacer(modifier = Modifier.height(40.dp))
                PageNavigator(
                    page = uiState.page,
                    onLeftClick = viewModel::moveToPreviousPage,
                    onRightClick = viewModel::moveToNextPage,
                    canMoveToPreviousPage = !viewModel.isStartPage(),
                    canMoveToNextPage = !viewModel.isEndPage(),
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview
@Composable
private fun CartScreenPreview() {
    CartScreen(
        onCloseClick = {},
        activityFinish = {},
    )
}
