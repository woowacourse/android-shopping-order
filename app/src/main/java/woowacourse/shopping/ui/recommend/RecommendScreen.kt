@file:Suppress("FunctionName")

package woowacourse.shopping.ui.recommend

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductTitle
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.ui.component.ProductItem
import woowacourse.shopping.ui.cart.ShoppingCartTopBar
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun RecommendScreen(
    recommendedProducts: List<ShoppingItem>,
    baseSelectedCartItemCount: Int,
    totalPrice: Int,
    onBackClick: () -> Unit,
    onOrderButtonClick: (List<Long>) -> Unit,
    onAddToCartClick: (ShoppingItem) -> Unit,
    onQuantityPlusClick: (ShoppingItem) -> Unit,
    onQuantityMinusClick: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIdsInCurrentList =
        recommendedProducts
            .filter { shoppingItem -> shoppingItem.getQuantity() > 0 }
            .map { shoppingItem -> shoppingItem.getProductId() }
    val orderItemCount = baseSelectedCartItemCount + selectedIdsInCurrentList.size
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
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "이런 상품은 어떠세요?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (recommendedProducts.isEmpty()) {
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
                        items = recommendedProducts,
                        key = { recommendedProduct -> recommendedProduct.getProductId() },
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
            OrderButton(
                onOrderButtonClick = onOrderButtonClick,
                selectedShoppingCartItemIds = selectedIdsInCurrentList,
                orderItemCount = orderItemCount,
                totalPrice = totalPrice,
            )
        }
    }
}

@Composable
fun OrderButton(
    onOrderButtonClick: (List<Long>) -> Unit,
    selectedShoppingCartItemIds: List<Long>,
    orderItemCount: Int,
    totalPrice: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
                DecimalFormat(stringResource(R.string.price_format_pattern)).format(
                    totalPrice,
                ),
            modifier =
                Modifier
                    .weight(2f)
                    .padding(end = 16.dp),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Button(
            onClick = { onOrderButtonClick(selectedShoppingCartItemIds) },
            modifier =
                Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
            shape = RectangleShape,
            enabled = orderItemCount > 0,
        ) {
            Text(text = "주문하기($orderItemCount)")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendScreenPreview() {
    AndroidShoppingTheme {
        RecommendScreen(
            recommendedProducts =
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
