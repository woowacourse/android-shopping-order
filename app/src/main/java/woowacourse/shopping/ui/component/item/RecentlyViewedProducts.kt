package woowacourse.shopping.ui.component.item

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
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products

@Composable
fun RecentlyViewedProducts(
    products: Products,
    onClick: (Product) -> Unit,
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
                items = products.products,
                key = { index, product -> "${product.id}_i$index}" },
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
    RecentlyViewedProducts(
        Products(
            products =
                listOf(
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                    Product(
                        imageUri = "hello",
                        name = "너무너무너무긴아이템이름",
                        price = 100000,
                        category = "",
                        id = 1L,
                    ),
                ),
        ),
        onClick = { },
    )
}
