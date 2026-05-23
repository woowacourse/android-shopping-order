package woowacourse.shopping.presentation.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.components.CartBottomBar
import woowacourse.shopping.presentation.cart.components.CartCheckBox
import woowacourse.shopping.presentation.cart.components.CartContent
import woowacourse.shopping.presentation.cart.components.CartPageSection
import woowacourse.shopping.presentation.cart.components.SkeletonCartContent
import woowacourse.shopping.presentation.cart.model.CartItemUiModel
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.util.formattedPrice

@Composable
fun CartItemListScreen(
    onBack: () -> Unit,
    onOrderClick: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartItemListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CartItemListContent(
        currentCartItems = uiState.currentCartItems,
        isLoading = uiState.isLoading,
        isShowPageSection = uiState.isShowPageSection,
        page = uiState.page,
        isCanMoveNext = uiState.isCanMoveNext,
        totalQuantity = uiState.totalQuantity,
        totalPrice = uiState.totalPrice,
        isAllItemsSelected = uiState.isSelectAll,
        onBackClick = onBack,
        onNextPageClick = viewModel::nextPage,
        onPreviousPageClick = viewModel::previousPage,
        onItemSelected = viewModel::selectItem,
        onItemDeleted = viewModel::deleteItem,
        onIncreaseQuantity = viewModel::addItemToCart,
        onDecreaseQuantity = viewModel::deleteItem,
        onOrderClick = {
            onOrderClick(
                viewModel.getPaymentItemIds(),
            )
        },
        onSelectAllItemClick = viewModel::toggleSelectAll,
        modifier = modifier,
    )
}

@Composable
fun CartItemListContent(
    currentCartItems: List<CartItemUiModel>,
    isLoading: Boolean,
    isShowPageSection: Boolean,
    page: Int,
    isCanMoveNext: Boolean,
    totalQuantity: Int,
    totalPrice: Long,
    isAllItemsSelected: Boolean,
    onBackClick: () -> Unit,
    onNextPageClick: () -> Unit,
    onPreviousPageClick: () -> Unit,
    onItemSelected: (Long) -> Unit,
    onItemDeleted: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    onOrderClick: () -> Unit,
    onSelectAllItemClick: () -> Unit,
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
                                .clickable { onBackClick() },
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
                if (isShowPageSection) {
                    CartPageSection(
                        page = page + 1,
                        onNext = { onNextPageClick() },
                        onPrevious = { onPreviousPageClick() },
                        isCanMoveNext = isCanMoveNext,
                    )
                }
                CartBottomBar(
                    purchaseItemCount = totalQuantity,
                    totalPrice = formattedPrice(totalPrice),
                    onOrderClick = { onOrderClick() },
                    allCheckBox = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp),
                        ) {
                            CartCheckBox(
                                isSelected = isAllItemsSelected,
                                onClick = onSelectAllItemClick,
                                modifier = Modifier.padding(0.dp),
                            )
                            Text(
                                text = stringResource(R.string.total),
                                color = Color.White,
                                fontSize = 12.sp,
                            )
                        }
                    },
                    modifier = Modifier.navigationBarsPadding(),
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
            if (isLoading) {
                SkeletonCartContent(modifier = Modifier.fillMaxSize())
                return@Box
            }
            CartContent(
                onSelected = { onItemSelected(it) },
                onDeleteItem = { onItemDeleted(it) },
                cartItems = currentCartItems,
                onIncrease = { onIncreaseQuantity(it) },
                onDecrease = { onDecreaseQuantity(it) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun CartItemListContentPreview() {
    AndroidshoppingTheme {
        CartItemListContent(
            currentCartItems =
                listOf(
                    CartItemUiModel(
                        product = ProductUiModel(id = 1L, name = "아메리카노", price = 6000L, imageUrl = ""),
                        quantity = 2,
                        isSelected = true,
                    ),
                    CartItemUiModel(
                        product = ProductUiModel(id = 2L, name = "카페라떼", price = 5500L, imageUrl = ""),
                        quantity = 1,
                        isSelected = false,
                    ),
                ),
            isLoading = false,
            isShowPageSection = true,
            page = 0,
            isCanMoveNext = true,
            totalQuantity = 3,
            totalPrice = 17500L,
            isAllItemsSelected = false,
            onBackClick = {},
            onNextPageClick = {},
            onPreviousPageClick = {},
            onItemSelected = {},
            onItemDeleted = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onOrderClick = {},
            onSelectAllItemClick = {},
        )
    }
}
