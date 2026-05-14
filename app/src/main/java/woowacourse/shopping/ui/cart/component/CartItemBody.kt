package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.fixture.MockProducts
import woowacourse.shopping.ui.cart.list.CartItemUiModel

@Composable
fun CartItemBody(
    items: List<CartItemUiModel>,
    showPagination: Boolean,
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
    onItemCheckedChange: (Long, Boolean) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = items, key = { it.productId.toString() }) { item ->
            CartItemUnit(
                item = item,
                onCheckedChange = { isChecked -> onItemCheckedChange(item.productId, isChecked) },
                onDeleteClick = { onDeleteClick(item.productId) },
                onIncreaseQuantity = { onIncreaseQuantity(item.productId) },
                onDecreaseQuantity = { onDecreaseQuantity(item.productId) },
            )
        }

        if (showPagination) {
            item {
                Spacer(modifier = Modifier.height(15.dp))

                CartPaging(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CartItemBodyPreview() {
    CartItemBody(
        items =
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
            ),
        onItemCheckedChange = { _, _ -> },
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        showPagination = true,
        currentPage = 1,
        totalPages = 5,
        onPreviousClick = {},
        onNextClick = {},
    )
}
