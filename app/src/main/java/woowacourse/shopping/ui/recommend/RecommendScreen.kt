package woowacourse.shopping.ui.recommend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.cart.RecommendProductContent
import woowacourse.shopping.ui.component.ShoppingAppBar

@Composable
fun RecommendScreen(
    uiState: RecommendUiState,
    onBackClick: () -> Unit,
    onQuantityChange: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBackClick() },
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                    Text(
                        text = "Cart",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        when {
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                )
            }

            else -> {
                RecommendProductContent(
                    products = uiState.products,
                    onQuantityChange = onQuantityChange,
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .padding(start = 12.dp, end = 12.dp, top = 100.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecommendScreenPreview() {
    RecommendScreen(
        uiState = RecommendUiState(),
        onBackClick = {},
        onQuantityChange = { _, _ -> },
    )
}
