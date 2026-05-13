package woowacourse.shopping.presentation.cart.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.model.CartUiState
import woowacourse.shopping.presentation.cart.ui.components.CartBottomBar
import woowacourse.shopping.presentation.cart.ui.components.CartContent
import woowacourse.shopping.presentation.cart.ui.components.CartPageSection
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.util.formattedPrice

@Composable
fun CartScreen(
    uiState: CartUiState,
    onBack: () -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onSelected: (Long) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
    onOrderClick: () -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBack() },
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                    Text(
                        text = "Cart",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
        bottomBar = {
            Column {
                if (uiState.isShowPageSection) {
                    CartPageSection(
                        page = uiState.page + 1,
                        onNext = { onNextPage() },
                        onPrevious = { onPreviousPage() },
                        isCanMoveNext = uiState.isCanMoveNext,
                    )
                }
                CartBottomBar(
                    purchaseItemCount = 5,
                    totalPrice = formattedPrice(5000),
                    isSelectAll = true,
                    onOrderClick = { onOrderClick() },
                    onClickCheckBox = { onSelectAll() },
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            CartContent(
                isLoading = uiState.isLoading,
                onSelected = { onSelected(it) },
                onDeleteItem = { onDeleteItem(it) },
                cartItems = uiState.currentCartItems.toImmutableList(),
                onIncrease = { onIncrease(it) },
                onDecrease = { onDecrease(it) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun CartScreenPreview() {
    CartScreen(
        uiState =
            CartUiState(
                totalPrice = 5_000,
                totalQuantity = 5,
                isSelectAll = true,
            ),
        onBack = {},
        onDecrease = {},
        onIncrease = {},
        onDeleteItem = {},
        onNextPage = {},
        onPreviousPage = {},
        onSelected = {},
        onOrderClick = {},
        onSelectAll = {},
    )
}
