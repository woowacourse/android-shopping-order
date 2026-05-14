package woowacourse.shopping.ui.cart.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.cart.common.CartBottomBar
import woowacourse.shopping.ui.cart.common.CartHeader
import woowacourse.shopping.ui.common.component.network.NetworkStatusBanner
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.component.ProductUnit
import woowacourse.shopping.ui.theme.ShoppingColors.Gray4

@Composable
fun CartRecommendedProductsScreen(
    recommendedProducts: List<ShoppingProductUiState>,
    totalPrice: String,
    selectedCount: Int,
    isLoading: Boolean,
    isNetworkConnected: Boolean,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Long) -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    onOrderClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        CartHeader(onBackClick = onBackClick)
        if (!isNetworkConnected) {
            NetworkStatusBanner(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp))
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),

        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Box
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "이런 상품은 어떠세요?",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.W700,
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "* 가장 최근에 본 상품의 카테고리에서 추천 상품을 보여드려요.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = Gray4,
                )
                Spacer(modifier = Modifier.size(29.dp))

                if (recommendedProducts.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "추천할 상품이 없습니다.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
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
        CartBottomBar(
            totalPrice = totalPrice,
            selectedCount = selectedCount,
            onOrderClick = onOrderClick,
            modifier = Modifier.fillMaxWidth(),
            showSelectAll = false,
        )
    }
}
