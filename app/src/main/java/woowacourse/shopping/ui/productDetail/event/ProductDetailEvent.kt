package woowacourse.shopping.ui.productDetail.event

sealed interface ProductDetailEvent {
    data object SaveProductInHistory : ProductDetailEvent
    data object AddProductToCart : ProductDetailEvent
}