package woowacourse.shopping.ui.shopping

sealed class ShoppingUiEvent {
    object NavToCart : ShoppingUiEvent()

    object NavToSetting : ShoppingUiEvent()

    data class NavToDetail(
        val productId: String,
    ) : ShoppingUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : ShoppingUiEvent()
}
