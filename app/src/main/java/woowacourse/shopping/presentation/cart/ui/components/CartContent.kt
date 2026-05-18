package woowacourse.shopping.presentation.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.presentation.cart.model.CartItemUiModel
import woowacourse.shopping.presentation.common.model.MockModels
import woowacourse.shopping.presentation.common.model.MockModels.product

private const val SKELETON_CARD_SIZE = 5

@Composable
fun CartContent(
    isLoading: Boolean,
    onSelected: (Long) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
    cartItems: ImmutableList<CartItemUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        if (isLoading && cartItems.isEmpty()) {
            items(count = SKELETON_CARD_SIZE) {
                SkeletonCartCard()
            }
        }
        items(
            items = cartItems,
            key = { it.product.id },
        ) { item ->
            val product = item.product
            CartCard(
                productName = product.name,
                price = item.totalPrice,
                imageUrl = product.imageUrl,
                quantity = item.quantity,
                onDeleteItem = {
                    onDeleteItem(product.id)
                },
                onIncrease = {
                    onIncrease(product.id)
                },
                onDecrease = {
                    onDecrease(product.id)
                },
                isSelected = item.isSelected,
                onSelectProduct = { onSelected(item.product.id) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartContentPreview() {
    CartContent(
        isLoading = false,
        onSelected = {},
        onDeleteItem = {},
        onIncrease = {},
        onDecrease = {},
        cartItems =
            listOf(
                MockModels.cartItem,
                MockModels.cartItem.copy(
                    product = product.copy(id = 2L, name = "아메리카노"),
                ),
            ).toImmutableList(),
    )
}
