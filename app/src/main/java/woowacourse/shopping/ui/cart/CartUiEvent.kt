package woowacourse.shopping.ui.cart

sealed class CartUiEvent {
    object NavToBack : CartUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : CartUiEvent()
}
