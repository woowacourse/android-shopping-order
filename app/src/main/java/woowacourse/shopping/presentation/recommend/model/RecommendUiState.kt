package woowacourse.shopping.presentation.recommend.model

import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel

data class RecommendUiState(
    val recommendProducts: List<ShoppingItemUiModel> = emptyList(),
    val paymentItems: PaymentItems = PaymentItems(emptySet()),
) {
    val totalPrice: Long get() = paymentItems.totalPrice
    val totalQuantity: Int get() = paymentItems.totalQuantity
}
