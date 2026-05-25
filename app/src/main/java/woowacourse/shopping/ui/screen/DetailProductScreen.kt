@file:Suppress("FunctionName")

package woowacourse.shopping.ui.screen

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import woowacourse.shopping.R
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.component.detailproduct.DetailProductTopBar
import woowacourse.shopping.ui.component.detailproduct.LastViewedProductSection
import woowacourse.shopping.ui.component.productlist.ProductQuantityBox
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun DetailProductScreen(
    quantity: Int,
    quantityPrice: Int,
    shoppingItem: ShoppingItem,
    lastViewedShoppingItem: ShoppingItem?,
    onAddToCartClick: () -> Unit,
    onLastViewedProductClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val product = shoppingItem.getProduct()
    Scaffold(
        topBar = {
            DetailProductTopBar(
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription =
                    stringResource(
                        R.string.product_image_content_description,
                        product.getTitle(),
                    ),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
            )
            Text(
                text = product.getTitle(),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp),
            )
            HorizontalDivider()
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        DecimalFormat(stringResource(R.string.price_format_pattern)).format(
                            quantityPrice,
                        ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                ProductQuantityBox(
                    onQuantityPlusClick = onQuantityPlusClick,
                    onQuantityMinusClick = onQuantityMinusClick,
                    quantity = quantity,
                    modifier =
                        Modifier
                            .padding(
                                start = 7.5.dp,
                                top = 8.dp,
                            ).width(104.dp),
                )
            }
            if (lastViewedShoppingItem != null && lastViewedShoppingItem.getProductId() != shoppingItem.getProductId()) {
                LastViewedProductSection(
                    shoppingItem = lastViewedShoppingItem,
                    onLastViewedProductClick = onLastViewedProductClick,
                    modifier = Modifier.padding(16.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onAddToCartClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RectangleShape,
            ) {
                Text(stringResource(R.string.add_to_cart_button_text))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DetailProductScreenPreview() {
    AndroidShoppingTheme {
        DetailProductScreen(
            shoppingItem =
                ShoppingItem(
                    product =
                        Product(
                            id = 1,
                            title = ProductTitle("동원 스위트콘"),
                            price = Price(99_800),
                            imageUrl = "https://img.dongwonmall.com/dwmall/static_root/model_img/main/153/15327_1_a.jpg?f=webp&q=80",
                        ),
                    quantity = 0,
                ),
            lastViewedShoppingItem =
                ShoppingItem(
                    product =
                        Product(
                            id = 1,
                            title = ProductTitle("동원 스위트콘"),
                            price = Price(99_800),
                            imageUrl = "https://img.dongwonmall.com/dwmall/static_root/model_img/main/153/15327_1_a.jpg?f=webp&q=80",
                        ),
                    quantity = 0,
                ),
            onAddToCartClick = {},
            onLastViewedProductClick = {},
            onBackClick = {},
            quantity = 0,
            quantityPrice = 0,
            onQuantityPlusClick = {},
            onQuantityMinusClick = {},
        )
    }
}
