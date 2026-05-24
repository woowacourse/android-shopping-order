package woowacourse.shopping.ui.cart.recommend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.cart.CartBottomBar
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.model.ProductUiModel

@Composable
fun RecommendProductScreenRoute(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    RecommendProductScreen(
        products = uiState.recommendProducts,
        totalPrice = uiState.totalPrice,
        totalCount = uiState.totalCartQuantity,
        isLoading = uiState.isRecommendLoading,
        onBackClick = onBackClick,
        onQuantityChange = cartViewModel::updateQuantityAndSelect,
        onOrderClick = onOrderClick,
        modifier = modifier,
    )
}

@Composable
fun RecommendProductScreen(
    products: ImmutableList<ProductUiModel>,
    totalPrice: Long,
    totalCount: Int,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
    onOrderClick: () -> Unit,
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
        bottomBar = {
            CartBottomBar(
                isAllChecked = false,
                totalPrice = totalPrice,
                totalCount = totalCount,
                onAllCheckedChange = {},
                onOrderClick = onOrderClick,
                showCheckBox = false,
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        RecommendProductContent(
            products = products,
            isLoading = isLoading,
            onQuantityChange = onQuantityChange,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(start = 12.dp, end = 12.dp, top = 100.dp),
        )
    }
}

@Preview
@Composable
private fun RecommendProductScreenPreview() {
    RecommendProductScreen(
        products =
            persistentListOf(
                ProductUiModel(
                    id = 1L,
                    name = "상품1",
                    imageUrl = "",
                    price = 1000L,
                    quantity = 0,
                ),
                ProductUiModel(
                    id = 2L,
                    name = "상품2",
                    imageUrl = "",
                    price = 2000L,
                    quantity = 1,
                ),
                ProductUiModel(
                    id = 3L,
                    name = "상품3",
                    imageUrl = "",
                    price = 2000L,
                    quantity = 1,
                ),
            ),
        totalPrice = 20000L,
        totalCount = 4,
        isLoading = false,
        onBackClick = {},
        onQuantityChange = { _, _ -> },
        onOrderClick = {},
    )
}
