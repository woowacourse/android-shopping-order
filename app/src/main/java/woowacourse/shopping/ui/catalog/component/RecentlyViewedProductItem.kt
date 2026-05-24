package woowacourse.shopping.ui.catalog.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.core.designsystem.component.ProductImage
import woowacourse.shopping.core.designsystem.component.toPriceString
import woowacourse.shopping.ui.uimodel.ProductUiModel

@Composable
fun RecentlyViewedProductItem(
    product: ProductUiModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(100.dp)
                .height(120.dp)
                .clickable(
                    onClick = {
                        onClick(product.id)
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProductImage(
            imageUri = product.imageUrl,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
        )
        Text(
            text = product.name,
            fontWeight = FontWeight.W700,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedProductItemPreview() {
    RecentlyViewedProductItem(
        ProductUiModel(
            id = 1L,
            imageUrl = "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcREOx9x8uZchUa41cKYxYrqv5uj-bD4zupCW4G3ADchbwNbXaxRIZtdeG9clkH0F06NCsQnTQ690KD0G4PygBj6ZPVbvCS7KUEmMwETqd9c7xuGRnAFucVgDQhFmfK2FJ3XWHAcKw&usqp=CAc",
            name = "너무너무너무너무너무너무너무너무 긴 이름",
            price = 1000,
            formattedPrice = 1000.toPriceString(),
            category = "카테고리",
        ),
        onClick = {},
    )
}
