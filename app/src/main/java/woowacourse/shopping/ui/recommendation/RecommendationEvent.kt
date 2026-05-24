package woowacourse.shopping.ui.recommendation

import woowacourse.shopping.domain.PurchaseProduct

sealed interface RecommendationEvent {
    data class SnackbarEvent(
        val errorMsg: String,
    ) : RecommendationEvent

    data class AddToCart(
        val purchaseProduct: PurchaseProduct,
    ) : RecommendationEvent

    data class UpdateAmount(
        val targetID: Long,
        val updateAmount: Int,
    ) : RecommendationEvent

    data class RemoveFromCart(
        val targetId: Long,
    ) : RecommendationEvent

    data class NavigateToPayment(
        val checkedIds: List<Long>,
    ) : RecommendationEvent

    object NavigateToCart : RecommendationEvent
}
