package woowacourse.shopping.ui.cart.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.cart.common.CartBottomBar
import woowacourse.shopping.ui.cart.common.CartHeader
import woowacourse.shopping.ui.cart.list.component.CartItemBody
import woowacourse.shopping.ui.cart.list.component.CartItemSkeletonBody
import woowacourse.shopping.ui.cart.list.uistate.CartItemUiModel
import woowacourse.shopping.ui.cart.list.uistate.CartListUiState
import woowacourse.shopping.ui.cart.list.uistate.CartUiState
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner
import woowacourse.shopping.ui.fixture.MockProducts

@Composable
fun CartScreen(
    uiState: CartUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    onItemCheckedChange: (Long, Boolean) -> Unit,
    onAllCheckedChange: (Boolean) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val cartListState = uiState.cartListState
    val cartItems = (cartListState as? CartListUiState.Content)?.items.orEmpty()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        CartHeader(onBackClick = onBackClick)
        if (!uiState.isNetworkConnected) {
            NetworkStatusBanner(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp))
        }

        when (cartListState) {
            CartListUiState.Loading -> {
                CartItemSkeletonBody(
                    modifier =
                        Modifier
                            .padding(top = 8.dp, start = 18.dp, end = 18.dp)
                            .weight(1f),
                )
            }

            is CartListUiState.Content -> {
                CartItemBody(
                    items = cartListState.items,
                    showPagination = cartListState.totalPages > 1,
                    currentPage = cartListState.currentPage,
                    totalPages = cartListState.totalPages,
                    modifier =
                        Modifier
                            .padding(top = 8.dp, start = 18.dp, end = 18.dp)
                            .weight(1f),
                    onItemCheckedChange = onItemCheckedChange,
                    onDeleteClick = onDeleteClick,
                    onIncreaseQuantity = onIncreaseQuantity,
                    onDecreaseQuantity = onDecreaseQuantity,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                )
            }

            is CartListUiState.Error -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = cartListState.message ?: "상품을 불러오지 못했습니다.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
        CartBottomBar(
            totalPrice = uiState.totalPrice,
            selectedCount = uiState.totalSelectedCount,
            onOrderClick = onOrderClick,
            modifier = Modifier.fillMaxWidth(),
            showSelectAll = cartItems.isNotEmpty(),
            isAllSelected = uiState.isAllSelected,
            onAllSelectedChanged = onAllCheckedChange,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CartScreenPreview() {
    val items =
        listOf(
            CartItemUiModel(
                cartItemId = MockProducts.APPLE.id,
                productId = MockProducts.APPLE.id,
                name = MockProducts.APPLE.name,
                imageUrl = MockProducts.APPLE.imageUrl,
                price = MockProducts.APPLE.price.value,
                quantity = 2,
            ),
            CartItemUiModel(
                cartItemId = MockProducts.BBOYAMI.id,
                productId = MockProducts.BBOYAMI.id,
                name = MockProducts.BBOYAMI.name,
                imageUrl = MockProducts.BBOYAMI.imageUrl,
                price = MockProducts.BBOYAMI.price.value,
                quantity = 1,
            ),
        )
    CartScreen(
        uiState =
            CartUiState(
                totalPrice = 10000,
                totalSelectedCount = 2,
                isAllSelected = false,
            ),
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
        onAllCheckedChange = {},
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}

@Composable
@Preview(showBackground = true, name = "장바구니 로딩")
private fun CartScreenLoadingPreview() {
    CartScreen(
        uiState =
            CartUiState(
                totalPrice = 10000,
                totalSelectedCount = 2,
                isAllSelected = false,
            ),
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
        onAllCheckedChange = {},
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}

@Composable
@Preview(showBackground = true, name = "장바구니 에러")
private fun CartScreenErrorPreview() {
    CartScreen(
        uiState =
            CartUiState(
                totalPrice = 10000,
                totalSelectedCount = 2,
                isAllSelected = false,
            ),
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
        onAllCheckedChange = {},
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}
