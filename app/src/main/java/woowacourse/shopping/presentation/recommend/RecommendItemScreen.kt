package woowacourse.shopping.presentation.recommend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.getValue
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
import woowacourse.shopping.presentation.cart.components.CartBottomBar
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.presentation.productlist.model.ShoppingItemUiModel
import woowacourse.shopping.presentation.recommend.components.RecommendSection
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.util.formattedPrice

@Composable
fun RecommendItemScreen(
    onBackClick: () -> Unit,
    onOrderClick: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecommendItemViewModel = viewModel(factory = RecommendItemViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecommendItemContent(
        totalQuantity = uiState.totalQuantity,
        totalPrice = uiState.totalPrice,
        recommendProducts = uiState.recommendProducts,
        onBackClick = onBackClick,
        onOrderClick = { onOrderClick(viewModel.getPaymentItemIds()) },
        onIncreaseQuantity = viewModel::addItemToCart,
        onDecreaseQuantity = viewModel::removeItemFromCart,
        modifier = modifier,
    )
}

@Composable
fun RecommendItemContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    onIncreaseQuantity: (Long) -> Unit,
    onDecreaseQuantity: (Long) -> Unit,
    totalQuantity: Int,
    totalPrice: Long,
    recommendProducts: List<ShoppingItemUiModel>,
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
        bottomBar = {
            CartBottomBar(
                purchaseItemCount = totalQuantity,
                totalPrice = formattedPrice(totalPrice),
                onOrderClick = { onOrderClick() },
                allCheckBox = {},
                modifier = Modifier.navigationBarsPadding(),
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
                items = recommendProducts,
                onIncrease = onIncreaseQuantity,
                onDecrease = onDecreaseQuantity,
            )
        }
    }
}

@Preview
@Composable
private fun RecommendItemContentPreview() {
    AndroidshoppingTheme {
        RecommendItemContent(
            totalQuantity = 3,
            totalPrice = 18000L,
            recommendProducts =
                listOf(
                    ShoppingItemUiModel(
                        product = ProductUiModel(id = 1L, name = "아메리카노", price = 6000L, imageUrl = ""),
                        quantity = 1,
                    ),
                    ShoppingItemUiModel(
                        product = ProductUiModel(id = 2L, name = "카페라떼", price = 6000L, imageUrl = ""),
                        quantity = 2,
                    ),
                ),
            onBackClick = {},
            onOrderClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
        )
    }
}
