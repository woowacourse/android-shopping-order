package woowacourse.shopping.ui.productDetail.event

sealed interface ProductDetailEvent {
    data object SaveProductInHistory : ProductDetailEvent
    data object AddProductToCart : ProductDetailEvent
    data object Finish : ProductDetailEvent
    data class NavigateToProductDetail(val productId: Long) : ProductDetailEvent
}