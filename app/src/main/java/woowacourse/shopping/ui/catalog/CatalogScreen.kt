package woowacourse.shopping.ui.catalog

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.ui.catalog.component.CountBadge
import woowacourse.shopping.ui.catalog.component.RecentlyViewedProducts
import woowacourse.shopping.core.designsystem.component.layout.CommonFrame
import woowacourse.shopping.core.designsystem.component.ShoppingItem

@Composable
fun CatalogScreen(
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
    modifier: Modifier = Modifier,
) {
    CommonFrame(
        headerContent = { CatalogHeader(totalCount, onCartClick) },
        bodyContent = {
            CatalogBody(
                catalog = catalog,
                recentlyViewedProducts = recentlyViewedProducts,
                onRecentlyViewedClick = onRecentlyViewedClick,
                onItemClick = { onItemClick(it) },
                onLoadClick = onLoadClick,
                onAdd = { id, updateAmount ->
                    onAdd(id, updateAmount)
                },
                onMinus = { id, updateAmount ->
                    onMinus(id, updateAmount)
                },
                onDelete = { onDelete(it) },
                onAddInCart = { onAddInCart(it) },
                specificProductCount = {
                    specificProductCount(it)
                },
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
            text = "Shopping",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        Row {
            Icon(
                painter = painterResource(R.drawable.ic_cart),
                contentDescription = "장바구니 아이콘",
                tint = Color.White,
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable(onClick = onCartClick),
            )
            CountBadge(totalCount)
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
                    onAddInCart = { onAddInCart(it) },
                )
            }
            if(isLoading){
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    CatalogLoadingScreen()
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
        contentDescription = "더보기 버튼",
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
private fun CatalogScreenPreview() {
    val mockProduct = Product(
            imageUri = "hello",
            name = "너무너무너무긴아이템이름",
            price = 100000,
            category = "카테고리",
            id = 1L,
        )
    val catalog = Products(List(10) { index -> mockProduct.copy(id = index + 1L) })

    CatalogScreen(
        catalog,
        catalog,
        onRecentlyViewedClick = {},
        totalCount = 10,
        specificProductCount = { it -> 0 },
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
        isContainedInCart = { it -> true },
        isLoading = false,
    )
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPreview2() {
    val previewProduct =
        Product(
            imageUri = "hello",
            name = "너무너무너무긴아이템이름",
            price = 100000,
            category = "카테고리",
            id = 1L,
        )
    val catalog = Products(List(9) { index -> previewProduct.copy(id = index + 1L) })

    CatalogScreen(
        catalog,
        Products(),
        onRecentlyViewedClick = {},
        totalCount = 10,
        specificProductCount = { it -> 0 },
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
        isContainedInCart = { it -> true },
        isLoading = true,
    )
}
