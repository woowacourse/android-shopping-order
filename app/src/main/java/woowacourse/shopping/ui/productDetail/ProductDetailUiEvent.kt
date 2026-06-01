package woowacourse.shopping.ui.productDetail

sealed interface ProductDetailUiEvent {
    data class ShowSnackbar(val message: String) : ProductDetailUiEvent
    data object AddedToCart: ProductDetailUiEvent
}
