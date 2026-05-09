@file:Suppress("FunctionName")

package woowacourse.shopping.ui

import android.icu.text.DecimalFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import woowacourse.shopping.ui.component.ProductQuantityBox
import woowacourse.shopping.ui.component.ShoppingCardAddBox
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun ProductListScreen(
    shoppingItems: List<ShoppingItem>,
    shoppingCartTotalCount: Int,
    onAddToCartClick: (ShoppingItem) -> Unit,
    onQuantityPlusClick: (ShoppingItem) -> Unit,
    onQuantityMinusClick: (ShoppingItem) -> Unit,
    onNavigateToCartClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            ProductListTopBar(
                shoppingCartTotalCount = shoppingCartTotalCount,
                onNavigateToCartClick = onNavigateToCartClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = innerPadding,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp),
        ) {
            items(
                items = shoppingItems,
                key = { it.getProductId() },
            ) { shoppingItem ->
                ProductItem(
                    product = shoppingItem.getProduct(),
                    quantity = shoppingItem.getQuantity(),
                    onAddToCartClick = { onAddToCartClick(shoppingItem) },
                    onQuantityPlusClick = { onQuantityPlusClick(shoppingItem) },
                    onQuantityMinusClick = { onQuantityMinusClick(shoppingItem) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable { onProductClick(shoppingItem.getProductId()) },
                )
            }

            if (bottomContent != null) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    bottomContent()
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    quantity: Int,
    onAddToCartClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = stringResource(
                R.string.product_image_content_description,
                product.getTitle()
            ),
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(154.dp)
                    .padding(bottom = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        Text(
            text = product.getTitle(),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier =
                Modifier.padding(
                    horizontal = 7.5.dp,
                ),
        )
        Text(
            text = DecimalFormat(stringResource(R.string.price_format_pattern)).format(product.getPrice()),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier.padding(
                    horizontal = 7.5.dp,
                ),
        )
        if (quantity == 0) {
            ShoppingCardAddBox(
                onShoppingCartAddClick = onAddToCartClick,
                modifier =
                    Modifier
                        .padding(
                            start = 7.5.dp,
                            top = 8.dp,
                        )
                        .size(36.dp),
            )
        } else {
            ProductQuantityBox(
                onQuantityPlusClick = onQuantityPlusClick,
                onQuantityMinusClick = onQuantityMinusClick,
                quantity = quantity,
                modifier =
                    Modifier
                        .padding(
                            start = 7.5.dp,
                            top = 8.dp,
                        )
                        .width(104.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopBar(
    shoppingCartTotalCount: Int,
    onNavigateToCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
                IconButton(onClick = onNavigateToCartClick) {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                    Image(
                        painter = painterResource(R.drawable.shopping_cart_icon),
                        contentDescription = stringResource(R.string.cart_icon_description),
                        modifier = Modifier.size(22.dp),
                    )
                    if (shoppingCartTotalCount > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                                Modifier
                                    .size(18.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                    ),
                        ) {
                            Text(
                                text = shoppingCartTotalCount.coerceAtMost(99).toString(),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun ProductItemPreview() {
    ProductItem(
        product =
            Product(
                id = 1,
                title = ProductTitle("동원 스위트콘"),
                price = Price(99_800),
                imageUrl = "https://img.dongwonmall.com/dwmall/static_root/model_img/main/153/15327_1_a.jpg?f=webp&q=80",
            ),
        quantity = 0,
        onAddToCartClick = {},
        onQuantityPlusClick = {},
        onQuantityMinusClick = {},
    )
}

@Composable
@Preview(showBackground = true)
private fun ProductListScreenPreview() {
    AndroidShoppingTheme {
        ProductListScreen(
            shoppingItems = emptyList(),
            shoppingCartTotalCount = 99,
            onAddToCartClick = {},
            onQuantityPlusClick = {},
            onQuantityMinusClick = {},
            onNavigateToCartClick = {},
            onProductClick = {},
        )
    }
}
