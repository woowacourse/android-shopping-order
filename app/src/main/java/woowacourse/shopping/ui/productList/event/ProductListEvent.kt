package woowacourse.shopping.ui.productList.event

sealed interface ProductListEvent {
    data class NavigateToProductDetail(val productId: Long) : ProductListEvent
    data object NavigateToCart : ProductListEvent
}