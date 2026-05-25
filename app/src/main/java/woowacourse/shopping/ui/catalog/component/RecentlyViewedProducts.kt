package woowacourse.shopping.ui.catalog.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.ui.uimodel.ProductUiModel

@Composable
fun RecentlyViewedProducts(
    products: List<ProductUiModel>,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(20.dp),
    ) {
        Text(
            text = "최근 본 상품",
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            itemsIndexed(
                items = products,
                key = { index, product -> "${product.id}_i$index}" }
            ) { index, item ->
                RecentlyViewedProductItem(
                    product = item,
                    onClick = onClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedProductsPreview() {
    val mockProduct = ProductUiModel(
        imageUrl = "hello",
        name = "너무너무너무긴아이템이름",
        price = 100000,
        formattedPrice = 100000.toPriceString(),
        category = "카테고리",
        id = 1L,
    )
    val previewProducts = List(9) { index -> mockProduct.copy(id = index + 1L) }

    RecentlyViewedProducts(
        previewProducts,
        onClick = { },
    )
}
