@file:Suppress("FunctionName")

package woowacourse.shopping.ui.recommend

import androidx.compose.runtime.Composable
import woowacourse.shopping.domain.model.ShoppingItem

@Composable
internal fun ShoppingCartRecommendSection(
    recommendedShoppingItems: List<ShoppingItem>,
    baseSelectedCartItemCount: Int,
    totalPrice: Int,
    onBackClick: () -> Unit,
    onOrderButtonClick: (Set<Long>) -> Unit,
    onAddToCartClick: (ShoppingItem) -> Unit,
    onQuantityPlusClick: (ShoppingItem) -> Unit,
    onQuantityMinusClick: (ShoppingItem) -> Unit,
) {
    RecommentScreen(
        recommentProducts = recommendedShoppingItems,
        baseSelectedCartItemCount = baseSelectedCartItemCount,
        totalPrice = totalPrice,
        onBackClick = onBackClick,
        onOrderButtonClick = { selectedProductIds ->
            onOrderButtonClick(selectedProductIds.toSet())
        },
        onAddToCartClick = onAddToCartClick,
        onQuantityPlusClick = onQuantityPlusClick,
        onQuantityMinusClick = onQuantityMinusClick,
    )
}
