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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import woowacourse.shopping.ui.util.LoadState
import androidx.compose.foundation.lazy.grid.items as lazyGridItems
import androidx.compose.foundation.lazy.items as lazyRowItems

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onCartClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductListScreenContent(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        uiState = uiState,
        onAddClick = viewModel::addProduct,
        onIncrease = viewModel::increaseQuantity,
        onDecrease = viewModel::decreaseQuantity,
        onMoreClick = viewModel::getMoreProducts,
        onCartClick = onCartClick,
        onProductClick = onProductClick,
    )
}

@Composable
fun ProductListScreenContent(
    modifier: Modifier = Modifier,
    uiState: ProductListUiState,
    onAddClick: (Int) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onMoreClick: () -> Unit,
    onCartClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
) {
    Column(modifier = modifier) {
        ProductListTopAppBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            cartCount = uiState.totalCartAmount,
            showCartAmountBadge = uiState.showCartAmountBadge,
            onClick = onCartClick,
        )
        when (uiState.loadState) {
            is LoadState.Loading if uiState.products.isEmpty() -> {
                LoadingContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                )
            }

            is LoadState.Error -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    message = uiState.loadState.message ?: "알수 없는 에러입니다.",
                )
            }

            is LoadState.Initial -> {}

            else -> {
                ProductListContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    visibleProducts = uiState.products,
                    recentProducts = uiState.recentProducts,
                    hasNextPage = uiState.hasNextPage,
                    onProductClick = onProductClick,
                    onAddClick = { product -> onAddClick(product) },
                    onIncrease = { productId -> onIncrease(productId) },
                    onDecrease = { productId -> onDecrease(productId) },
                    onMoreClick = { onMoreClick() },
                )
            }
        }
        when (uiState.loadState) {
            is LoadState.Initial -> {}
            is LoadState.Loading -> {
                LoadingContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                )
            }

            is LoadState.Success -> {
                ProductListContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    visibleProducts = uiState.products,
                    recentProducts = uiState.recentProducts,
                    hasNextPage = uiState.hasNextPage,
                    onProductClick = onProductClick,
                    onAddClick = { product -> onAddClick(product) },
                    onIncrease = { productId -> onIncrease(productId) },
                    onDecrease = { productId -> onDecrease(productId) },
                    onMoreClick = { onMoreClick() },
                )
            }

            is LoadState.Error -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    message = uiState.loadState.message ?: "알수 없는 에러입니다.",
                )
            }
        }
    }
}

@Composable
private fun ProductListContent(
    visibleProducts: List<ProductUiModel>,
    recentProducts: List<Product>,
    hasNextPage: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit = {},
    onAddClick: (Int) -> Unit = {},
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
            products = visibleProducts,
            hasNextPage = hasNextPage,
            modifier = Modifier.weight(1f),
            onProductClick = onProductClick,
            onAddClick = onAddClick,
            onIncreaseClick = onIncrease,
            onDecreaseClick = onDecrease,
            onMoreClick = onMoreClick,
        )
    }
}

@Composable
private fun RecentProductsSection(
    recentProducts: List<Product>,
    onProductClick: (Int) -> Unit,
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
                    onClick = { onProductClick(product.id) },
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
    cartCount: String,
    showCartAmountBadge: Boolean,
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
        if (showCartAmountBadge) {
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
                    text = cartCount,
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
    cartCount: String,
    showCartAmountBadge: Boolean,
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
                showCartAmountBadge = showCartAmountBadge,
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
    products: List<ProductUiModel>,
    hasNextPage: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit = {},
    onAddClick: (Int) -> Unit = {},
    onIncreaseClick: (Int) -> Unit = {},
    onDecreaseClick: (Int) -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        lazyGridItems(
            items = products,
            key = { item -> item.id },
        ) { item ->
            ProductCard(
                modifier = Modifier.fillMaxWidth(),
                productName = item.name,
                price = item.price,
                imageUrl = item.imageUrl,
                quantity = item.cartAmount,
                showAmountController = item.showAmountController,
                onClick = { onProductClick(item.id) },
                onAddClick = { onAddClick(item.id) },
                onIncreaseClick = { onIncreaseClick(item.id) },
                onDecreaseClick = { onDecreaseClick(item.id) },
            )
        }
        if (hasNextPage) {
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
        uiState = ProductListUiState(),
        onAddClick = { },
        onIncrease = { },
        onDecrease = { },
        onMoreClick = { },
    )
}
