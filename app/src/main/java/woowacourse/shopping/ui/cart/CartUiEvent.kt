package woowacourse.shopping.ui.cart

sealed class CartUiEvent {
    object NavToBack : CartUiEvent()

    data class NavToPayment(
        val selectedCartItemIds: List<String>,
    ) : CartUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : CartUiEvent()
}
