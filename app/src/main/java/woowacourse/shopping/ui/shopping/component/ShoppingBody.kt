package woowacourse.shopping.ui.shopping.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.inmemory.InMemoryProductRepository
import woowacourse.shopping.ui.common.component.recentlyviewed.RecentlyViewedSection
import woowacourse.shopping.ui.shopping.ProductListUiState
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun ShoppingBody(
    productListState: ProductListUiState,
    recentProducts: List<Product>,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit,
    onMoreClick: () -> Unit,
    onAddToCart: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (recentProducts.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                RecentlyViewedSection(
                    products = recentProducts,
                    onProductClick = onProductClick,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(color = ShoppingColors.Gray1),
                )
            }
        }

        when (productListState) {
            ProductListUiState.Loading -> {
                items(6) {
                    ProductUnitSkeleton()
                }
            }

            is ProductListUiState.Content -> {
                items(
                    items = productListState.products,
                    key = {
                        it.product.id
                            .toString()
                    },
                ) { product ->
                    ProductUnit(
                        product = product,
                        onClick = { onProductClick(product.product) },
                        onAddToCart = { onAddToCart(product.product.id) },
                        onIncreaseQuantity = { onIncreaseQuantity(product.product.id) },
                        onDecreaseQuantity = { onDecreaseQuantity(product.product.id) },
                    )
                }

                if (productListState.hasNext) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            MoreButton(
                                modifier = Modifier,
                                onClick = onMoreClick,
                            )
                        }
                    }
                }
            }

            is ProductListUiState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = productListState.message ?: "상품을 불러오지 못했습니다.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductUnitSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .width(154.dp)
                .height(206.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(154.dp)
                    .background(ShoppingColors.Gray1),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier =
                Modifier
                    .padding(start = 6.dp, end = 9.dp)
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .background(ShoppingColors.Gray1),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier =
                Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .background(ShoppingColors.Gray1),
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ShoppingBodyPreview() {
    ShoppingBody(
        productListState =
            ProductListUiState.Content(
                products =
                    InMemoryProductRepository.products.toList().take(4).mapIndexed { index, product ->
                        ShoppingProductUiState(
                            product = product,
                            quantity = if (index % 2 == 0) 0 else 2,
                        )
                    },
                hasNext = true,
            ),
        recentProducts = InMemoryProductRepository.products.take(4),
        onProductClick = {},
        onMoreClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}

@Composable
@Preview(showBackground = true, name = "로딩 스켈레톤")
private fun ShoppingBodyLoadingPreview() {
    ShoppingBody(
        productListState = ProductListUiState.Loading,
        recentProducts = emptyList(),
        onProductClick = {},
        onMoreClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}

@Composable
@Preview(showBackground = true, name = "최근 본 상품과 로딩 스켈레톤")
private fun ShoppingBodyLoadingWithRecentProductsPreview() {
    ShoppingBody(
        productListState = ProductListUiState.Loading,
        recentProducts = InMemoryProductRepository.products.take(4),
        onProductClick = {},
        onMoreClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
