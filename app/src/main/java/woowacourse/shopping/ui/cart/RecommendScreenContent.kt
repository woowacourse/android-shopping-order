package woowacourse.shopping.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.domain.product.Category
import woowacourse.shopping.domain.product.ImageUrl
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductName
import woowacourse.shopping.ui.productList.ProductCard

@Composable
fun RecommendScreenContent(
    products: List<Product>,
    quantitiesByProductId: Map<Int, Int>,
    onAddClick: (Product) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text = "이런 상품은 어떠세요?", fontSize = 24.sp)
        Text(text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.", fontSize = 12.sp)

        Spacer(modifier = Modifier.height(30.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    modifier =
                        Modifier
                            .width(154.dp),
                    productName = product.name.value,
                    price = formatPrice(product.price.value),
                    imageUrl = product.imageUrl.value,
                    quantity = "${quantitiesByProductId[product.id] ?: 0}",
                    showAmountController = (quantitiesByProductId[product.id] ?: 0) > 0,
                    onClick = {},
                    onAddClick = { onAddClick(product) },
                    onIncreaseClick = { onIncrease(product.id) },
                    onDecreaseClick = { onDecrease(product.id) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendScreenPreview() {
    RecommendScreenContent(
        products =
            listOf(
                Product(
                    id = 1,
                    name = ProductName("상품명1"),
                    price = Price(100),
                    imageUrl = ImageUrl("https://picsum.photos/200"),
                    category = Category("dd"),
                ),
                Product(
                    id = 2,
                    name = ProductName("상품명2"),
                    price = Price(10077),
                    imageUrl = ImageUrl("https://picsum.photos/200"),
                    category = Category("dd"),
                ),
                Product(
                    id = 3,
                    name = ProductName("상품명3"),
                    price = Price(10077),
                    imageUrl = ImageUrl("https://picsum.photos/200"),
                    category = Category("dd"),
                ),
            ),
        quantitiesByProductId = mapOf(1 to 2, 2 to 0, 3 to 0),
        onAddClick = {},
        onIncrease = {},
        onDecrease = {},
        modifier = Modifier.background(Color.White),
    )
}
