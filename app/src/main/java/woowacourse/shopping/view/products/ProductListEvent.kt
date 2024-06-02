package woowacourse.shopping.view.products

sealed interface ProductListEvent {

    data object LoadProductEvent : ProductListEvent

    sealed interface UpdateProductEvent : ProductListEvent {
        data class Success(val productId: Long) : UpdateProductEvent
    }

    sealed interface DeleteProductEvent : ProductListEvent {
        data class Success(val productId: Long) : DeleteProductEvent
    }
}
