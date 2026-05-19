package woowacourse.shopping.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.ProductUiModel

@Composable
fun CartScreen(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onDeleteItem: (String) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onQuantityChange: (String, Int) -> Unit,
    onCheckedChange: (String) -> Unit,
    isAllSelectClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
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
            CartBottomBar(
                isOrder = uiState.isOrder,
                isAllChecked = uiState.isAllChecked,
                totalPrice = uiState.totalPrice,
                totalCount = uiState.selectedCartItemCount,
                onAllCheckedChange = isAllSelectClick,
                onOrderClick = onOrderClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        if (uiState.isOrder) {
            RecommendProductContent(
                products = uiState.recommendProducts,
                onQuantityChange = onQuantityChange,
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(start = 12.dp, end = 12.dp, top = 100.dp),
            )
        } else {
            CartContent(
                totalCartSize = uiState.totalCartCount,
                page = uiState.page,
                onNextPage = onNextPage,
                onPreviousPage = onPreviousPage,
                isCanMoveNext = uiState.isCanMoveNext,
                onQuantityChange = onQuantityChange,
                onDeleteItem = {
                    onDeleteItem(it)
                },
                cartItems = uiState.items,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onCheckedChange = onCheckedChange,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            )
        }
    }
}

@Composable
private fun CartContent(
    totalCartSize: Int,
    page: Int,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    isCanMoveNext: Boolean,
    onQuantityChange: (String, Int) -> Unit,
    onDeleteItem: (String) -> Unit,
    cartItems: ImmutableList<CartItemUiModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onCheckedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else if (isLoading) {
            items(count = 2) {
                CartCardSkeleton(
                    modifier =
                        Modifier
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                )
            }
        } else {
            items(
                items = cartItems,
                key = { it.id },
            ) { item ->
                val product = item.product
                CartCard(
                    productName = product.name,
                    price = item.totalPrice,
                    imageUrl = product.imageUrl,
                    quantity = item.quantity,
                    onQuantityChange = { quantity ->
                        onQuantityChange(product.id, quantity)
                    },
                    onDeleteItem = {
                        onDeleteItem(item.id)
                    },
                    isChecked = item.isChecked,
                    onCheckedChange = { onCheckedChange(item.id) },
                )
            }
        }
        if (totalCartSize > 5) {
            item {
                CartPageSection(
                    page = page + 1,
                    onNext = { onNextPage() },
                    onPrevious = { onPreviousPage() },
                    isCanMoveNext = isCanMoveNext,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CartScreenPreview() {
    CartScreen(
        uiState = CartUiState(),
        onBackClick = { },
        onDeleteItem = { },
        onNextPage = { },
        onPreviousPage = { },
        onQuantityChange = { _, _ -> },
        onCheckedChange = { },
        isAllSelectClick = { },
        onOrderClick = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun CartContentPreview() {
    CartContent(
        totalCartSize = 10,
        page = 0,
        onNextPage = {},
        onPreviousPage = {},
        isCanMoveNext = true,
        onDeleteItem = {},
        onQuantityChange = { _, _ -> },
        cartItems =
            listOf(
                CartItemUiModel(
                    product =
                        ProductUiModel(
                            id = "1",
                            name = "커피",
                            imageUrl = "",
                            price = 1000,
                        ),
                    quantity = 1,
                    totalPrice = 1000,
                    id = "",
                    isChecked = true,
                ),
                CartItemUiModel(
                    product =
                        ProductUiModel(
                            id = "2",
                            name = "커피",
                            imageUrl = "",
                            price = 1000,
                        ),
                    quantity = 1,
                    totalPrice = 1000,
                    id = "",
                    isChecked = true,
                ),
            ).toImmutableList(),
        isLoading = true,
        errorMessage = null,
        onCheckedChange = { },
    )
}
