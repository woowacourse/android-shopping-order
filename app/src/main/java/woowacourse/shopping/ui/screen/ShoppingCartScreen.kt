@file:Suppress("FunctionName")

package woowacourse.shopping.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.component.cart.CartSkeletonItem
import woowacourse.shopping.ui.component.cart.ShoppingCartItemCard
import woowacourse.shopping.ui.component.cart.ShoppingCartTopBar
import woowacourse.shopping.ui.state.ShoppingCartState
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun ShoppingCartScreen(
    shoppingCartItems: List<ShoppingCartItem>,
    state: ShoppingCartState,
    getQuantityPrice: (ShoppingCartItem) -> Int,
    onBackClick: () -> Unit,
    onRemoveShoppingItemClick: (ShoppingCartItem) -> Unit,
    onToggleShoppingItemSelectionClick: (Long, Boolean) -> Unit,
    onIncreaseShoppingItemQuantityClick: (ShoppingCartItem) -> Unit,
    onDecreaseShoppingItemQuantityClick: (ShoppingCartItem) -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ShoppingCartTopBar(
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.isLoading) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CartSkeletonItem()
                    CartSkeletonItem()
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    shoppingCartItems.forEach { shoppingCartItem ->
                        ShoppingCartItemCard(
                            shoppingCartItem = shoppingCartItem,
                            selected = shoppingCartItem.getId() in state.selectedCartItemIds,
                            quantityPrice = getQuantityPrice(shoppingCartItem),
                            onRemoveShoppingItemClick = onRemoveShoppingItemClick,
                            onToggleShoppingItemSelectionClick = onToggleShoppingItemSelectionClick,
                            onIncreaseShoppingItemQuantityClick = onIncreaseShoppingItemQuantityClick,
                            onDecreaseShoppingItemQuantityClick = onDecreaseShoppingItemQuantityClick,
                        )
                    }
                }
                bottomContent()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ShoppingCartScreenPreview() {
    AndroidShoppingTheme {
        ShoppingCartScreen(
            shoppingCartItems =
                listOf(
                    ShoppingCartItem(
                        id = 1,
                        shoppingItem =
                            ShoppingItem(
                                Product(
                                    1,
                                    ProductTitle("동원 스위트콘"),
                                    Price(99_800),
                                    "",
                                ),
                                4,
                            ),
                    ),
                    ShoppingCartItem(
                        id = 2,
                        shoppingItem =
                            ShoppingItem(
                                Product(
                                    1,
                                    ProductTitle("동원 스위트콘"),
                                    Price(99_800),
                                    "",
                                ),
                                4,
                            ),
                    ),
                    ShoppingCartItem(
                        id = 3,
                        shoppingItem =
                            ShoppingItem(
                                Product(
                                    1,
                                    ProductTitle("동원 스위트콘"),
                                    Price(99_800),
                                    "",
                                ),
                                4,
                            ),
                    ),
                ),
            state =
                ShoppingCartState(
                    items =
                        listOf(
                            ShoppingCartItem(
                                id = 1L,
                                shoppingItem =
                                    ShoppingItem(
                                        product =
                                            Product(
                                                id = 1L,
                                                title = ProductTitle("샘플 상품"),
                                                price = Price(12000),
                                                imageUrl = "https://example.com/image.jpg",
                                            ),
                                        quantity = 1,
                                    ),
                            ),
                        ),
                    isLoading = true,
                    errorMessage = null,
                    currentPage = 0,
                    canMoveToPreviousPage = false,
                    canMoveToNextPage = false,
                ),
            getQuantityPrice = { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() },
            onBackClick = {},
            onRemoveShoppingItemClick = {},
            onToggleShoppingItemSelectionClick = { _, _ -> },
            onIncreaseShoppingItemQuantityClick = {},
            onDecreaseShoppingItemQuantityClick = {},
        )
    }
}
