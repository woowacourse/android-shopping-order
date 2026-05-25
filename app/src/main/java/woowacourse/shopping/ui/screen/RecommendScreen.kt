@file:Suppress("FunctionName")

package woowacourse.shopping.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.component.cart.ShoppingCartTopBar
import woowacourse.shopping.ui.component.productlist.ProductItem
import woowacourse.shopping.ui.component.recommend.RecommendHeaderSection
import woowacourse.shopping.ui.component.recommend.RecommendOrderButton
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun RecommendScreen(
    recommentProducts: List<ShoppingItem>,
    baseSelectedCartItemCount: Int,
    totalPrice: Int,
    onBackClick: () -> Unit,
    onOrderButtonClick: () -> Unit,
    onAddToCartClick: (ShoppingItem) -> Unit,
    onQuantityPlusClick: (ShoppingItem) -> Unit,
    onQuantityMinusClick: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedRecommendedProductIds =
        recommentProducts
            .filter { shoppingItem -> shoppingItem.getQuantity() > 0 }
            .map { shoppingItem -> shoppingItem.getProductId() }
    val orderItemCount = baseSelectedCartItemCount + selectedRecommendedProductIds.size
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
                modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RecommendHeaderSection()

            if (recommentProducts.isEmpty()) {
                Text(
                    text = "추천 상품이 없습니다.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                LazyRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = recommentProducts,
                        key = { recommentProduct -> recommentProduct.getProductId() },
                    ) { shoppingItem ->
                        ProductItem(
                            product = shoppingItem.getProduct(),
                            quantity = shoppingItem.getQuantity(),
                            onAddToCartClick = { onAddToCartClick(shoppingItem) },
                            onQuantityPlusClick = { onQuantityPlusClick(shoppingItem) },
                            onQuantityMinusClick = { onQuantityMinusClick(shoppingItem) },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }
            RecommendOrderButton(
                onOrderButtonClick = onOrderButtonClick,
                orderItemCount = orderItemCount,
                totalPrice = totalPrice,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendScreenPreview() {
    AndroidShoppingTheme {
        RecommendScreen(
            recommentProducts =
                listOf(
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
            baseSelectedCartItemCount = 1,
            totalPrice = 12000,
            onOrderButtonClick = {},
            onAddToCartClick = {},
            onQuantityPlusClick = {},
            onQuantityMinusClick = {},
            onBackClick = {},
        )
    }
}
