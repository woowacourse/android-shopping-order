package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.common.model.ProductUiModel
import woowacourse.shopping.ui.common.theme.Gray5
import woowacourse.shopping.ui.shopping.component.ProductUnit

@Composable
fun CartRecommendationBody(
    productItems: List<ProductUiModel>,
    modifier: Modifier = Modifier,
    onIncreaseClick: (ProductUiModel) -> Unit,
    onDecreaseClick: (ProductUiModel) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Title(
            modifier = Modifier.padding(start = 16.dp),
        )
        Spacer(Modifier.height(29.dp))

        if (productItems.isNotEmpty()) {
            ItemRow(
                products = productItems,
                modifier = Modifier.padding(start = 12.dp),
                onIncreaseClick = onIncreaseClick,
                onDecreaseClick = onDecreaseClick,
            )
        } else {
            Text(
                text = "모든 상품을 담아서 추천 상품이 없습니다. 고객님 감사합니다.",
            )
        }
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "이런 상품은 어떠세요?",
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF333333),
        )
        Text(
            text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            color = Gray5,
        )
    }
}

@Composable
private fun ItemRow(
    products: List<ProductUiModel>,
    modifier: Modifier = Modifier,
    onIncreaseClick: (ProductUiModel) -> Unit,
    onDecreaseClick: (ProductUiModel) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = products.toList(), key = { it.product.id }) { uiModel ->
            ProductUnit(
                model = uiModel,
                modifier = Modifier,
                onClick = {},
                onIncreaseClick = { onIncreaseClick(uiModel) },
                onDecreaseClick = { onDecreaseClick(uiModel) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartRecommendationBodyPreview() {
    CartRecommendationBody(
        productItems =
            listOf(
                ProductUiModel(
                    Product(
                        name = "딸기라떼",
                        price = Money(4500),
                        imageUrl = "",
                    ),
                ),
                ProductUiModel(
                    Product(
                        name = "고구마라떼",
                        price = Money(5500),
                        imageUrl = "",
                    ),
                ),
            ),
        modifier = Modifier,
        onIncreaseClick = {},
        onDecreaseClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun TitlePreview() {
    Title()
}
