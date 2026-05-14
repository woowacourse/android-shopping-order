package woowacourse.shopping.feature.cart.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.feature.common.state.ProductUiModel

@Composable
fun CartItemList(
    isLoading: Boolean,
    cartContents: List<ProductUiModel>,
    onDelete: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (isLoading) {
            items(
                5,
            ) {
                CartItem(
                    isLoading = isLoading,
                    imageUrl = "",
                    name = "qweasdzxc",
                    price = 1000,
                    quantity = 3,
                    onDelete = {
                    },
                    onIncrease = {
                    },
                    onDecrease = {
                    },
                )
            }
        } else {
            items(
                key = { it.id },
                items = cartContents,
            ) {
                CartItem(
                    isLoading = isLoading,
                    imageUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                    quantity = it.quantity,
                    onDelete = {
                        onDelete(it.id)
                    },
                    onIncrease = {
                        onIncrease(it.id)
                    },
                    onDecrease = {
                        onDecrease(it.id)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CartItemListPreview() {
    CartItemList(
        cartContents = listOf(
            ProductUiModel(
                id = "1",
                name = "더미 상품 1",
                price = 10000,
                imageUrl = "",
                quantity = 1,
            ),
            ProductUiModel(
                id = "2",
                name = "더미 상품 2",
                price = 20000,
                imageUrl = "",
                quantity = 3,
            ),
        ),
        onDelete = {},
        onIncrease = {},
        onDecrease = {},
        isLoading = false,
    )
}
