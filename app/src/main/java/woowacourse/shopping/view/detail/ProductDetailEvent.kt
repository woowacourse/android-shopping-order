package woowacourse.shopping.view.detail

sealed interface ProductDetailEvent {

    sealed interface AddShoppingCart : ProductDetailEvent {
        data class Success(val productId: Long, val count: Int) : AddShoppingCart
    }

    sealed interface UpdateRecentlyProductItem : ProductDetailEvent {
        data object Success : UpdateRecentlyProductItem
    }
}
