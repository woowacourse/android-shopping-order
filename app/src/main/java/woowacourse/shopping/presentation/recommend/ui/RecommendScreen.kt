package woowacourse.shopping.presentation.recommend.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.ui.components.CartBottomBar
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.recommend.model.RecommendUiState
import woowacourse.shopping.presentation.recommend.ui.components.RecommendSection
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.util.formattedPrice

@Composable
fun RecommendScreen(
    uiState: RecommendUiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOrderClick: () -> Unit,
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBack() },
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
        bottomBar = {
            CartBottomBar(
                purchaseItemCount = uiState.totalQuantity,
                totalPrice = formattedPrice(uiState.totalPrice),
                onOrderClick = { onOrderClick() },
                allCheckBox = {},
            )
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            RecommendSection(
                modifier = Modifier.padding(innerPadding),
                items = uiState.recommendProducts.toImmutableList(),
                onIncrease = onIncrease,
                onDecrease = onDecrease,
            )
        }
    }
}

@Preview
@Composable
private fun RecommendScreenPreview() {
    AndroidshoppingTheme {
        RecommendScreen(
            uiState =
                RecommendUiState(
                    recommendProducts = emptyList(),
                ),
            onBack = {},
            onOrderClick = {},
            onIncrease = { },
            onDecrease = {},
        )
    }
}
