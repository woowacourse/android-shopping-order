package woowacourse.shopping.ui.detail

sealed class DetailUiEvent {
    object Dismiss : DetailUiEvent()

    data class NavToDetail(
        val productId: String,
    ) : DetailUiEvent()

    object NavToCart : DetailUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : DetailUiEvent()
}
