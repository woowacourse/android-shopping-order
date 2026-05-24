package woowacourse.shopping.ui.cart.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ui.cart.CartTopAppBar
import woowacourse.shopping.ui.cart.CartViewModel

@Composable
fun RecommendScreen(
    cartViewModel: CartViewModel,
    recommendViewModel: RecommendViewModel,
    onClickClose: () -> Unit,
    onNavigateToOrder: (List<Int>) -> Unit,
) {
    val cartUiState by cartViewModel.cartUiState.collectAsStateWithLifecycle()
    val recommendUiState by recommendViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        CartTopAppBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onClick = onClickClose,
        )
        RecommendScreenContent(
            recommendUiState = recommendUiState,
            totalPrice = cartUiState.totalPrice,
            totalCount = cartUiState.totalCount,
            onAddClick = cartViewModel::addCartItem,
            onIncrease = recommendViewModel::increaseRecommendProduct,
            onDecrease = recommendViewModel::decreaseRecommendProduct,
            onClickOrder = { onNavigateToOrder(cartViewModel.selectedCartIds.value.toList()) },
            modifier = Modifier.weight(1f),
        )
    }
}
