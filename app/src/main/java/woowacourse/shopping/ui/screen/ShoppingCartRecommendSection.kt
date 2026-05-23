@file:Suppress("FunctionName")

package woowacourse.shopping.ui.screen

import androidx.compose.runtime.Composable
import woowacourse.shopping.model.ShoppingItem

@Composable
internal fun ShoppingCartRecommendSection(
    recommendedShoppingItems: List<ShoppingItem>,
    baseSelectedCartItemCount: Int,
    totalPrice: Int,
    onBackClick: () -> Unit,
    onOrderButtonClick: () -> Unit,
    onAddToCartClick: (ShoppingItem) -> Unit,
    onQuantityPlusClick: (ShoppingItem) -> Unit,
    onQuantityMinusClick: (ShoppingItem) -> Unit,
) {
    RecommendScreen(
        recommentProducts = recommendedShoppingItems,
        baseSelectedCartItemCount = baseSelectedCartItemCount,
        totalPrice = totalPrice,
        onBackClick = onBackClick,
        onOrderButtonClick = onOrderButtonClick,
        onAddToCartClick = onAddToCartClick,
        onQuantityPlusClick = onQuantityPlusClick,
        onQuantityMinusClick = onQuantityMinusClick,
    )
}
