package woowacourse.shopping.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.ProductUiModel

@Composable
fun CartScreenRoute(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    CartScreen(
        cartItems = uiState.items,
        page = uiState.page,
        isCanMoveNext = uiState.isCanMoveNext,
        isLoading = uiState.isLoading,
        totalCartQuantity = uiState.totalCartQuantity,
        totalCartCount = uiState.totalCartCount,
        totalPrice = uiState.totalPrice,
        errorMessage = uiState.errorMessage,
        isAllChecked = uiState.isAllChecked,
        onBackClick = onBackClick,
        onDeleteItem = cartViewModel::deleteItem,
        onNextPage = cartViewModel::nextPage,
        onPreviousPage = cartViewModel::previousPage,
        onQuantityChange = cartViewModel::updateQuantity,
        onCheckedChange = cartViewModel::checkItem,
        isAllSelectClick = cartViewModel::isAllSelectClick,
        onOrderClick = {
            cartViewModel.loadRecommendProducts()
            onOrderClick()
        },
        modifier = modifier,
    )
}

@Composable
fun CartScreen(
    cartItems: ImmutableList<CartItemUiModel>,
    page: Int,
    isCanMoveNext: Boolean,
    isLoading: Boolean,
    totalCartQuantity: Int,
    totalCartCount: Long,
    totalPrice: Long,
    errorMessage: String?,
    isAllChecked: Boolean,
    onBackClick: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
    onCheckedChange: (Long) -> Unit,
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
                isAllChecked = isAllChecked,
                totalPrice = totalPrice,
                totalCount = totalCartQuantity,
                onAllCheckedChange = isAllSelectClick,
                onOrderClick = onOrderClick,
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        CartContent(
            totalCartSize = totalCartCount,
            page = page,
            onNextPage = onNextPage,
            onPreviousPage = onPreviousPage,
            isCanMoveNext = isCanMoveNext,
            onQuantityChange = onQuantityChange,
            onDeleteItem = {
                onDeleteItem(it)
            },
            cartItems = cartItems,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onCheckedChange = onCheckedChange,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        )
    }
}

@Composable
private fun CartContent(
    totalCartSize: Long,
    page: Int,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    isCanMoveNext: Boolean,
    onQuantityChange: (Long, Int) -> Unit,
    onDeleteItem: (Long) -> Unit,
    cartItems: ImmutableList<CartItemUiModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onCheckedChange: (Long) -> Unit,
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
                key = { it.product.id },
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
        cartItems = persistentListOf(),
        page = 0,
        isCanMoveNext = false,
        isLoading = false,
        totalCartQuantity = 0,
        totalCartCount = 0,
        totalPrice = 0,
        errorMessage = null,
        isAllChecked = false,
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
                            id = 1,
                            name = "커피",
                            imageUrl = "",
                            price = 1000,
                        ),
                    quantity = 1,
                    totalPrice = 1000,
                    id = 0,
                    isChecked = true,
                ),
                CartItemUiModel(
                    product =
                        ProductUiModel(
                            id = 2,
                            name = "커피",
                            imageUrl = "",
                            price = 1000,
                        ),
                    quantity = 1,
                    totalPrice = 1000,
                    id = 1,
                    isChecked = true,
                ),
            ).toImmutableList(),
        isLoading = true,
        errorMessage = null,
        onCheckedChange = { },
    )
}
