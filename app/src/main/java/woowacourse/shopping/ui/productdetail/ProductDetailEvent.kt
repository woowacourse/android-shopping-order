package woowacourse.shopping.ui.productdetail

sealed interface ProductDetailEvent {
    data class SnackbarEvent(
        val errorMsg: String,
    ) : ProductDetailEvent

    data class MoveToLastViewedProductDetail(
        val lastViewedProductId: Long,
    ) : ProductDetailEvent

    object MoveToShopping : ProductDetailEvent
}
