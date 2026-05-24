package woowacourse.shopping.ui.cart.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.ui.cart.recommend.RecommendUiState
import woowacourse.shopping.ui.productList.ProductCard

@Composable
fun RecommendScreenContent(
    recommendUiState: RecommendUiState,
    totalPrice: Int,
    totalCount: Int,
    onAddClick: (Int) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onClickOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "이런 상품은 어떠세요?", fontSize = 24.sp)
            Text(text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.", fontSize = 12.sp)

            Spacer(modifier = Modifier.height(30.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(recommendUiState.recommendProducts, key = { it.id }) { product ->
                    ProductCard(
                        modifier =
                            Modifier
                                .width(154.dp),
                        productName = product.name,
                        price = product.price,
                        imageUrl = product.imageUrl,
                        quantity = product.cartAmount,
                        showAmountController = product.showAmountController,
                        onClick = {},
                        onAddClick = { onAddClick(product.id) },
                        onIncreaseClick = { onIncrease(product.id) },
                        onDecreaseClick = { onDecrease(product.id) },
                    )
                }
            }
        }
        RecommentBottomBar(
            totalMoney = totalPrice,
            totalCount = totalCount,
            onClickOrder = onClickOrder,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(78.dp),
        )
    }
}

@Composable
fun RecommentBottomBar(
    totalMoney: Int,
    totalCount: Int,
    onClickOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color(0xff555555))
                    .fillMaxHeight()
                    .padding(14.dp)
                    .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = formatPrice(totalMoney),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .background(Color(0xff04C09E))
                    .padding(horizontal = 16.dp)
                    .clickable { onClickOrder() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "주문하기($totalCount)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendScreenPreview() {
    RecommendScreenContent(
        recommendUiState = RecommendUiState(),
        totalPrice = 0,
        totalCount = 0,
        onAddClick = {},
        onIncrease = {},
        onDecrease = {},
        onClickOrder = {},
        modifier = Modifier.background(Color.White),
    )
}
