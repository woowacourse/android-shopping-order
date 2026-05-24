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
import woowacourse.shopping.core.designsystem.component.ShoppingItem
import woowacourse.shopping.core.designsystem.component.layout.CommonFrame
import woowacourse.shopping.core.designsystem.component.toPriceString
import woowacourse.shopping.ui.catalog.component.CountBadge
import woowacourse.shopping.ui.catalog.component.RecentlyViewedProducts
import woowacourse.shopping.ui.uimodel.ProductUiModel

@Composable
fun CatalogScreen(
    uiState: CatalogUiState,
    onRecentlyViewedClick: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    onCartClick: () -> Unit,
    onLoadClick: () -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onAddInCart: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonFrame(
        headerContent = { CatalogHeader(uiState.totalCount, onCartClick) },
        bodyContent = {
            CatalogBody(
                uiState = uiState,
                onRecentlyViewedClick = onRecentlyViewedClick,
                onItemClick = onItemClick,
                onLoadClick = onLoadClick,
                onAdd = onAdd,
                onMinus = onMinus,
                onDelete = onDelete,
                onAddInCart = onAddInCart,
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
    uiState: CatalogUiState,
    onRecentlyViewedClick: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    onAddInCart: (Long) -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onLoadClick: () -> Unit,
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
                if (uiState.recentlyViewedProducts.isNotEmpty()) {
                    RecentlyViewedProducts(
                        uiState.recentlyViewedProducts,
                        onClick = onRecentlyViewedClick,
                    )
                }
            }

            items(uiState.products.size) { index ->
                val product = uiState.products[index]
                ShoppingItem(
                    product = product,
                    onClick = onItemClick,
                    count = {
                        uiState.productCount(product.id)
                    },
                    isContainedInCart = {
                        uiState.isContainedInCart(product.id)
                    },
                    onAdd = {
                        onAdd(product.id, 1)
                    },
                    onMinus = {
                        onMinus(product.id, -1)
                    },
                    onDelete = {
                        onDelete(product.id)
                    },
                    onAddInCart = onAddInCart,
                )
            }
            if (uiState.isLoading) {
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
    val mockProduct = ProductUiModel(
        imageUrl = "hello",
        name = "너무너무너무긴아이템이름",
        price = 100000,
        formattedPrice = 100000.toPriceString(),
        category = "카테고리",
        id = 1L,
    )
    val catalog = List(10) { index -> mockProduct.copy(id = index + 1L) }

    CatalogScreen(
        uiState = CatalogUiState(
            products = catalog,
            recentlyViewedProducts = catalog,
            totalCount = 10,
            isLoading = false,
        ),
        onRecentlyViewedClick = {},
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPreview2() {
    val previewProduct =
        ProductUiModel(
            imageUrl = "hello",
            name = "너무너무너무긴아이템이름",
            price = 100000,
            formattedPrice = 100000.toPriceString(),
            category = "카테고리",
            id = 1L,
        )
    val catalog = List(9) { index -> previewProduct.copy(id = index + 1L) }

    CatalogScreen(
        uiState = CatalogUiState(
            products = catalog,
            totalCount = 10,
            isLoading = true,
        ),
        onRecentlyViewedClick = {},
        onItemClick = { },
        onCartClick = { },
        onLoadClick = { },
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = { },
        onAddInCart = { },
    )
}
