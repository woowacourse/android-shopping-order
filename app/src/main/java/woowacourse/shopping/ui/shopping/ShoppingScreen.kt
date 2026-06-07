package woowacourse.shopping.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.cart.item.CartCountLabel
import woowacourse.shopping.ui.common.frame.CommonFrame
import woowacourse.shopping.ui.shopping.items.LoadCatalog
import woowacourse.shopping.ui.shopping.items.RecentlyViewedProducts
import woowacourse.shopping.ui.shopping.items.ShoppingItem

@Composable
fun ShoppingScreen(
    catalog: Products,
    recentlyViewedProducts: Products,
    onRecentlyViewedClick: (Product) -> Unit,
    totalCount: Int,
    specificProductCount: (Long) -> Int,
    onItemClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onLoadClick: () -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onAddInCart: (PurchaseProduct) -> Unit,
    isLoading: Boolean,
    isContainedInCart: (Long) -> Boolean,
    allowNotification: Boolean,
    onSwitchNotification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonFrame(
        headerContent = {
            CatalogHeader(
                totalCount,
                onCartClick,
                allowNotification = allowNotification,
                onSwitchNotification = onSwitchNotification,
            )
        },
        bodyContent = {
            CatalogBody(
                catalog = catalog,
                recentlyViewedProducts = recentlyViewedProducts,
                onRecentlyViewedClick = onRecentlyViewedClick,
                onItemClick = onItemClick,
                onLoadClick = onLoadClick,
                onAdd = onAdd,
                onMinus = onMinus,
                onDelete = onDelete,
                onAddInCart = onAddInCart,
                specificProductCount = specificProductCount,
                isContainedInCart = isContainedInCart,
                isLoading = isLoading,
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun CatalogHeader(
    totalCount: Int,
    onCartClick: () -> Unit,
    allowNotification: Boolean,
    onSwitchNotification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.title_shopping),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        Row {
            Icon(
                painter = painterResource(R.drawable.ic_notification),
                contentDescription = stringResource(R.string.cb_notification_icon),
                tint = Color.White,
            )
            Switch(
                checked = allowNotification,
                onCheckedChange = { onSwitchNotification() },
                colors =
                    SwitchDefaults.colors(
                        checkedTrackColor = Color(0xFF04C09E),
                    ),
            )
        }
        Row {
            Icon(
                painter = painterResource(R.drawable.ic_cart),
                contentDescription = stringResource(R.string.cd_cart_icon),
                tint = Color.White,
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable(onClick = onCartClick),
            )
            CartCountLabel(totalCount)
        }
    }
}

@Composable
private fun CatalogBody(
    catalog: Products,
    recentlyViewedProducts: Products,
    onRecentlyViewedClick: (Product) -> Unit,
    specificProductCount: (Long) -> Int,
    onItemClick: (Product) -> Unit,
    onAddInCart: (PurchaseProduct) -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onLoadClick: () -> Unit,
    isLoading: Boolean,
    isContainedInCart: (Long) -> Boolean,
    modifier: Modifier = Modifier,
) {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier,
            contentPadding = PaddingValues(12.dp),
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                if (!recentlyViewedProducts.isEmpty()) {
                    RecentlyViewedProducts(
                        recentlyViewedProducts,
                        onClick = onRecentlyViewedClick,
                    )
                }
            }

            items(catalog.size()) { item ->
                ShoppingItem(
                    product = catalog.getSingleItem(item),
                    onClick = {
                        onItemClick(catalog.getSingleItem(item))
                    },
                    count = {
                        specificProductCount(catalog.getSingleItem(item).id)
                    },
                    isContainedInCart = {
                        isContainedInCart(catalog.getSingleItem(item).id)
                    },
                    onAdd = {
                        onAdd(catalog.getSingleItem(item).id, 1)
                    },
                    onMinus = {
                        onMinus(catalog.getSingleItem(item).id, -1)
                    },
                    onDelete = {
                        onDelete(catalog.getSingleItem(item).id)
                    },
                    onAddInCart = onAddInCart,
                )
            }
            if (isLoading) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    LoadCatalog()
                }
            }
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                LoadBtn(
                    onLoad = onLoadClick,
                )
            }
        }
    }
}

@Composable
private fun LoadBtn(
    onLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(R.drawable.ic_add),
        contentDescription = stringResource(R.string.cd_load_more_button),
        tint = Color.White,
        modifier =
            modifier
                .padding(25.dp)
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.LightGray)
                .clickable(onClick = onLoad),
    )
}

@Preview(showBackground = true)
@Composable
private fun ShoppingScreenPreview() {
    val catalog =
        Products(
            listOf(
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "패션",
                    id = 1L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "가전",
                    id = 2L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "식품",
                    id = 3L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "도서",
                    id = 4L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "완구",
                    id = 5L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "가구",
                    id = 6L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "스포츠",
                    id = 7L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "잡화",
                    id = 8L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "뷰티",
                    id = 9L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "반려동물",
                    id = 10L,
                ),
            ),
        )

    ShoppingScreen(
        catalog,
        catalog,
        onRecentlyViewedClick = {},
        totalCount = 10,
        specificProductCount = { 0 },
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
        isContainedInCart = { true },
        isLoading = false,
        allowNotification = false,
        onSwitchNotification = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun ShoppingScreenPreview2() {
    val catalog =
        Products(
            listOf(
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "패션",
                    id = 1L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "가전",
                    id = 2L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "식품",
                    id = 3L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "도서",
                    id = 4L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "완구",
                    id = 5L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "가구",
                    id = 6L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "스포츠",
                    id = 7L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "잡화",
                    id = 8L,
                ),
                Product(
                    imageUri = "hello",
                    name = "너무너무너무긴아이템이름",
                    price = 100000,
                    category = "뷰티",
                    id = 9L,
                ),
            ),
        )

    ShoppingScreen(
        catalog,
        Products(),
        onRecentlyViewedClick = {},
        totalCount = 10,
        specificProductCount = { 0 },
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
        isContainedInCart = { true },
        isLoading = true,
        allowNotification = true,
        onSwitchNotification = { },
    )
}
