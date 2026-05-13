package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.repository.inmemory.InMemoryProductRepository
import woowacourse.shopping.ui.cart.component.CartBottomBar
import woowacourse.shopping.ui.cart.component.CartHeader
import woowacourse.shopping.ui.cart.component.CartItemBody
import woowacourse.shopping.ui.cart.component.CartItemSkeletonBody
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner

@Composable
fun CartScreen(
    cartListState: CartListUiState,
    isNetworkConnected: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onOrderClick: (Set<Long>) -> Unit,
    onItemCheckedChange: (Long, Boolean) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val cartItems = (cartListState as? CartListUiState.Content)?.items.orEmpty()
    val selectedItems = cartItems.filter { it.isSelected }
    val selectedProductIds = selectedItems.map { it.productId }.toSet()
    val totalPrice = selectedItems.sumOf { it.price * it.quantity }
    val isAllSelected = cartItems.isNotEmpty() && cartItems.all { it.isSelected }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        CartHeader(onBackClick = onBackClick)
        if (!isNetworkConnected) {
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
            totalPrice = formatPrice(totalPrice),
            selectedCount = selectedItems.size,
            onOrderClick = { onOrderClick(selectedProductIds) },
            modifier = Modifier.fillMaxWidth(),
            showSelectAll = cartItems.isNotEmpty(),
            isAllSelected = isAllSelected,
            onAllSelectedChanged = { isSelected ->
                cartItems.forEach { item ->
                    onItemCheckedChange(item.productId, isSelected)
                }
            },
        )
    }
}

private fun formatPrice(totalPrice: Int): String = "%,d원".format(totalPrice)

@Composable
@Preview(showBackground = true)
private fun CartScreenPreview() {
    val items =
        listOf(
            CartItemUiModel(
                cartItemId = InMemoryProductRepository.APPLE.id,
                productId = InMemoryProductRepository.APPLE.id,
                name = InMemoryProductRepository.APPLE.name,
                imageUrl = InMemoryProductRepository.APPLE.imageUrl,
                price = InMemoryProductRepository.APPLE.price.value,
                quantity = 2,
            ),
            CartItemUiModel(
                cartItemId = InMemoryProductRepository.BBOYAMI.id,
                productId = InMemoryProductRepository.BBOYAMI.id,
                name = InMemoryProductRepository.BBOYAMI.name,
                imageUrl = InMemoryProductRepository.BBOYAMI.imageUrl,
                price = InMemoryProductRepository.BBOYAMI.price.value,
                quantity = 1,
            ),
        )
    CartScreen(
        cartListState =
            CartListUiState.Content(
                items = items,
                currentPage = 1,
                totalPages = 1,
                hasPrevious = false,
                hasNext = false,
            ),
        isNetworkConnected = true,
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
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
        cartListState = CartListUiState.Loading,
        isNetworkConnected = true,
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
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
        cartListState = CartListUiState.Error("장바구니를 불러오지 못했습니다."),
        isNetworkConnected = true,
        onBackClick = {},
        onOrderClick = {},
        onItemCheckedChange = { _, _ -> },
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}
