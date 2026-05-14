package woowacourse.shopping.ui.productList

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import woowacourse.shopping.R
import woowacourse.shopping.constant.ShoppingColor.APP_BAR_COLOR
import woowacourse.shopping.domain.product.Product
import androidx.compose.foundation.lazy.grid.items as lazyGridItems
import androidx.compose.foundation.lazy.items as lazyRowItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel,
    onCartClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartCount = (uiState as? ProductListUiState.Success)?.totalCartCount ?: 0

    ProductListScreenContent(
        modifier = modifier,
        uiState = uiState,
        cartCount = cartCount,
        onAddClick = viewModel::addProduct,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onMoreClick = viewModel::moreProducts,
        onCartClick = onCartClick,
        onProductClick = onProductClick,
    )
}

@Composable
fun ProductListScreenContent(
    modifier: Modifier = Modifier,
    uiState: ProductListUiState,
    cartCount: Int,
    onAddClick: (Product) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onMoreClick: () -> Unit,
    onCartClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
) {
    Column(modifier = modifier) {
        ProductListTopAppBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            cartCount = cartCount,
            onClick = onCartClick,
        )

        when (uiState) {
            is ProductListUiState.Loading -> {
                LoadingContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                )
            }

            is ProductListUiState.Success -> {
                ProductListContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    visibleProducts = uiState.products,
                    recentProducts = uiState.recentProducts,
                    quantitiesByProductId = uiState.quantitiesByProductId,
                    canLoadMore = uiState.canLoadMore,
                    isLoadingMore = uiState.isLoadingMore,
                    onProductClick = onProductClick,
                    onAddClick = { product -> onAddClick(product) },
                    onIncrease = { productId -> onIncrease(productId) },
                    onDecrease = { productId -> onDecrease(productId) },
                    onMoreClick = { onMoreClick() },
                )
            }

            is ProductListUiState.Error -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    message = uiState.message,
                )
            }
        }
    }
}

@Composable
private fun ProductListContent(
    visibleProducts: List<Product>,
    recentProducts: List<Product>,
    quantitiesByProductId: Map<Int, Int>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},
    onAddClick: (Product) -> Unit = {},
    onIncrease: (Int) -> Unit = {},
    onDecrease: (Int) -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        if (recentProducts.isNotEmpty()) {
            RecentProductsSection(
                recentProducts = recentProducts,
                onProductClick = onProductClick,
            )
        }
        ProductCardGrid(
            visibleProducts = visibleProducts,
            quantitiesByProductId = quantitiesByProductId,
            canLoadMore = canLoadMore,
            isLoadingMore = isLoadingMore,
            modifier = Modifier.weight(1f),
            onProductClick = onProductClick,
            onAddClick = onAddClick,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            onMoreClick = onMoreClick,
        )
    }
}

@Composable
private fun RecentProductsSection(
    recentProducts: List<Product>,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = 20.dp)) {
        Text(
            text = "최근 본 상품",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            lazyRowItems(
                items = recentProducts,
                key = { product -> product.id },
            ) { product ->
                RecentProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                )
            }
        }
    }
}

@Composable
private fun RecentProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(96.dp)
                .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = product.imageUrl.value,
            contentDescription = product.name.value,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(Color(0xFFF4F4F4)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = product.name.value,
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CartBadgeIcon(
    cartCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = "장바구니 아이콘",
            )
        }
        if (cartCount > 0) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 4.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1ABC9C)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$cartCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopAppBar(
    cartCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Shopping",
                fontSize = 20.sp,
            )
        },
        actions = {
            CartBadgeIcon(
                cartCount = cartCount,
                onClick = onClick,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(APP_BAR_COLOR),
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}

@Composable
private fun ProductCardGrid(
    visibleProducts: List<Product>,
    quantitiesByProductId: Map<Int, Int>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},
    onAddClick: (Product) -> Unit = {},
    onIncrease: (Int) -> Unit = {},
    onDecrease: (Int) -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        lazyGridItems(
            items = visibleProducts,
            key = { item -> item.id },
        ) { item ->
            ProductCard(
                modifier = Modifier.fillMaxWidth(),
                imageUrl = item.imageUrl.value,
                productName = item.name.value,
                price = item.price.value,
                quantity = quantitiesByProductId[item.id] ?: 0,
                onClick = { onProductClick(item) },
                onAddClick = { onAddClick(item) },
                onIncrease = { onIncrease(item.id) },
                onDecrease = { onDecrease(item.id) },
            )
        }
        if (isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (canLoadMore) {
            item(span = { GridItemSpan(2) }) {
                MoreButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 20.dp)
                            .background(
                                color = Color(0xFF555555),
                                shape = RoundedCornerShape(size = 45.dp),
                            ),
                ) {
                    onMoreClick()
                }
            }
        }
    }
}

@Composable
private fun MoreButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text("더보기")
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    ProductListScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState = ProductListUiState.Loading,
//            ProductListUiState.Success(
//                products = emptyList(),
//                recentProducts = emptyList(),
//                quantitiesByProductId = emptyMap(),
//                canLoadMore = true,
//                isLoadingMore = false,
//                totalCartCount = 0,
//            ),
        cartCount = 0,
        onAddClick = {},
        onIncrease = {},
        onDecrease = {},
        onMoreClick = {},
    )
}
