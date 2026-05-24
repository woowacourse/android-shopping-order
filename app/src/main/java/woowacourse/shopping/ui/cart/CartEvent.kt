package woowacourse.shopping.ui.cart

sealed interface CartEvent {
    data class SnackbarEvent(
        val errorMsg: String,
    ) : CartEvent

    data class UpdateCount(
        val targetId: Long,
        val updateAmount: Int,
    ) : CartEvent

    data class RemoveFromCart(
        val targetId: Long,
    ) : CartEvent

    object NextPage : CartEvent

    object PrevPage : CartEvent

    object NavigateToShopping : CartEvent

    data class NavigateToRecommendation(
        val totalPrice: Int,
        val checkedIds: List<Long>,
    ) : CartEvent
}
