package woowacourse.shopping.presentation.productlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.presentation.productlist.components.CartIcon
import woowacourse.shopping.presentation.productlist.components.LoadButton
import woowacourse.shopping.presentation.productlist.components.ProductCard
import woowacourse.shopping.presentation.productlist.components.RecentSection
import woowacourse.shopping.presentation.productlist.components.SkeletonProductsContent
import woowacourse.shopping.presentation.productlist.model.ShoppingItemUiModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

@Composable
fun ProductListScreen(
    onNavigateToCart: () -> Unit,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductListContent(
        isLoading = uiState.isLoading,
        products = uiState.products,
        recentProducts = uiState.recentProducts,
        loadMoreEnabled = uiState.canLoadMore,
        totalQuantity = uiState.totalQuantity,
        errorMessage = uiState.errorMessage,
        onNavigateToCartClick = onNavigateToCart,
        onLoadMoreClick = viewModel::loadMoreProducts,
        onIncreaseQuantityClick = viewModel::addItemToCart,
        onDecreaseQuantityClick = viewModel::removeItemFromCart,
        onProductClick = onProductClick,
        modifier = modifier,
    )
}

@Composable
fun ProductListContent(
    products: List<ShoppingItemUiModel>,
    recentProducts: List<ProductUiModel>,
    isLoading: Boolean,
    loadMoreEnabled: Boolean,
    totalQuantity: Int,
    errorMessage: String?,
    onNavigateToCartClick: () -> Unit,
    onLoadMoreClick: () -> Unit,
    onIncreaseQuantityClick: (Long) -> Unit,
    onDecreaseQuantityClick: (Long) -> Unit,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f),
                    )
                    CartIcon(
                        quantity = totalQuantity,
                        isShowCartQuantityIcon = totalQuantity > 0,
                        onNavigateToCart = onNavigateToCartClick,
                    )
                },
                modifier = modifier.fillMaxWidth(),
            )
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = Color.Gray,
                    fontSize = 18.sp,
                )
            }
            if (isLoading && products.isEmpty()) {
                SkeletonProductsContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                )
                return@Box
            } else {
                ProductList(
                    items = products,
                    onLoadMoreClick = onLoadMoreClick,
                    loadMoreEnabled = loadMoreEnabled,
                    onProductClick = onProductClick,
                    onIncreaseQuantityClick = onIncreaseQuantityClick,
                    onDecreaseQuantityClick = onDecreaseQuantityClick,
                    recentProducts = recentProducts,
                )
            }
        }
    }
}

@Composable
private fun ProductList(
    items: List<ShoppingItemUiModel>,
    recentProducts: List<ProductUiModel>,
    onLoadMoreClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onIncreaseQuantityClick: (Long) -> Unit,
    onDecreaseQuantityClick: (Long) -> Unit,
    loadMoreEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 20.dp),
        ) {
            item(
                span = { GridItemSpan(2) },
            ) {
                RecentSection(
                    recentProducts = recentProducts,
                    onClick = {
                        onProductClick(it)
                    },
                )
            }
            items(
                items = items,
                key = { it.product.id },
            ) { item ->
                ProductCard(
                    product = item.product,
                    quantity = item.quantity,
                    onClick = { onProductClick(item.product.id) },
                    onIncrease = { onIncreaseQuantityClick(item.product.id) },
                    onDecrease = { onDecreaseQuantityClick(item.product.id) },
                )
            }
            if (loadMoreEnabled) {
                item(
                    span = { GridItemSpan(2) },
                ) {
                    LoadButton(
                        onClick = onLoadMoreClick,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProductListContentPreview() {
    AndroidshoppingTheme {
        ProductListContent(
            products = sampleProducts,
            recentProducts = sampleRecentProducts,
            isLoading = false,
            loadMoreEnabled = true,
            totalQuantity = 7,
            errorMessage = null,
            onNavigateToCartClick = {},
            onLoadMoreClick = {},
            onIncreaseQuantityClick = {},
            onDecreaseQuantityClick = {},
            onProductClick = {},
        )
    }
}

private val sampleProducts =
    List(4) { index ->
        ShoppingItemUiModel(
            product =
                ProductUiModel(
                    id = index.toLong(),
                    name = "상품 ${index + 1}",
                    price = (1000L * (index + 1)),
                    imageUrl = "",
                ),
            quantity = if (index % 3 != 0) index else 0,
        )
    }

private val sampleRecentProducts =
    List(3) { index ->
        ProductUiModel(
            id = (100 + index).toLong(),
            name = "최근 본 상품 ${index + 1}",
            price = 5000L,
            imageUrl = "",
        )
    }
