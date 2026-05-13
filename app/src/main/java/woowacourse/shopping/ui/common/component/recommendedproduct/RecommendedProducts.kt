package woowacourse.shopping.ui.common.component.recommendedproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.inmemory.InMemoryProductRepository
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.component.ProductUnit
import woowacourse.shopping.ui.theme.ShoppingColors.Gray4

@Composable
fun RecommendedProducts(
    recommendedProducts: List<ShoppingProductUiState>,
    isLoading: Boolean,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(20.dp))
            return
        }

        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "이런 상품은 어떠세요?",
                fontSize = 36.sp,
                fontWeight = FontWeight.W700,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = Gray4
            )
            Spacer(modifier = Modifier.size(29.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = recommendedProducts,
                    key = { it.product.id.toString() },
                ) { product ->
                    ProductUnit(
                        product = product,
                        onClick = { onProductClick(product.product) },
                        onAddToCart = { onAddToCart(product.product.id) },
                        onIncreaseQuantity = { onIncreaseQuantity(product.product.id) },
                        onDecreaseQuantity = { onDecreaseQuantity(product.product.id) },
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecommendedProductsPreview() {
    RecommendedProducts(
        recommendedProducts =
            InMemoryProductRepository.products.take(4).mapIndexed { index, product ->
                ShoppingProductUiState(
                    product = product,
                    quantity = if (index % 2 == 0) 0 else 1,
                )
            },
        isLoading = false,
        onProductClick = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
