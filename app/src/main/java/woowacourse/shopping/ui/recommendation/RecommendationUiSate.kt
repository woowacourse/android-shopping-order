package woowacourse.shopping.ui.recommendation

import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProducts

data class RecommendationUiSate(
    val recommendedProducts: Products = Products(),
    val totalPrice: Int = 0,
    val cart: PurchaseProducts = PurchaseProducts(),
    val checkedIds: List<Long> = emptyList(),
)
