package woowacourse.shopping.ui.component.detailproduct

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun LastViewedProductSection(
    shoppingItem: ShoppingItem,
    onLastViewedProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val product = shoppingItem.getProduct()
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp))
                .clickable { onLastViewedProductClick(product.id) }
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.last_viewed_product_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = product.getTitle(),
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun LastViewedProductSectionPreview() {
    AndroidShoppingTheme {
        LastViewedProductSection(
            shoppingItem =
                ShoppingItem(
                    product =
                        Product(
                            id = 2,
                            title = ProductTitle("오리온 카스타드"),
                            price = Price(4_800),
                            imageUrl = "https://img.dongwonmall.com/dwmall/static_root/model_img/main/153/15327_1_a.jpg?f=webp&q=80",
                        ),
                    quantity = 0,
                ),
            onLastViewedProductClick = {},
        )
    }
}
