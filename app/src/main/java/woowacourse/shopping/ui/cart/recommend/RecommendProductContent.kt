package woowacourse.shopping.ui.cart.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.shopping.ProductCard
import woowacourse.shopping.ui.theme.Gray50

@Composable
fun RecommendProductContent(
    products: ImmutableList<ProductUiModel>,
    onQuantityChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "이런 상품은 어떠세요?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "* 최근 본 상품을 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Gray50,
        )

        Spacer(modifier = Modifier.height(29.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = products,
                key = { it.id },
            ) {
                ProductCard(
                    onQuantityChange = { quantity ->
                        onQuantityChange(it.id, quantity)
                    },
                    productName = it.name,
                    imageUrl = it.imageUrl,
                    price = it.price,
                    quantity = it.quantity ?: 0,
                    modifier =
                        Modifier
                            .width(154.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecommendProductContentPreview() {
    RecommendProductContent(
        products = persistentListOf(),
        onQuantityChange = { _, _ -> },
    )
}
