package woowacourse.shopping.feature.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.feature.cart.component.CartAppBar
import woowacourse.shopping.feature.format.DecimalPriceFormatter

@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = viewModel(
        factory = RecommendViewModel.Factory,
    ),
    onCloseClick: () -> Unit,
    contentIds: List<Long>,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.initialLoading()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            CartAppBar(onCloseClick = onCloseClick)
        },
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            Text("이런 상품은 어떠세요?")
            Text("* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.")
            RecommendList(
                isLoading = false,
                products = uiState.recommendList,
                onIncrease = viewModel::increase,
                onDecrease = viewModel::decrease,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .background(Color(0xff555555)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        DecimalPriceFormatter().format(
                            viewModel.getTotalPrice(
                                contentIds = contentIds,
                            ),
                        ),
                        fontWeight = FontWeight.W700,
                        fontSize = 18.sp,
                        color = Color.White,
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.order(contentIds)
                    },
                    modifier = Modifier
                        .width(122.dp)
                        .fillMaxHeight()
                        .background(Color(0xff04C09E)),
                ) {
                    Text(
                        "주문하기(${
                            viewModel.getTotalCount(
                                contentIds = contentIds,
                            )
                        })",
                        fontWeight = FontWeight.W700,
                        fontSize = 18.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecommendScreenPreview() {
    RecommendScreen(
        onCloseClick = {},
        contentIds = emptyList(),
    )
}
